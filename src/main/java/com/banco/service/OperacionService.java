package com.banco.service;

import com.banco.entity.Cliente;
import com.banco.entity.Cuenta;
import com.banco.entity.Movimiento;
import com.banco.entity.MovimientoId;
import com.banco.event.NotificacionEvent;
import com.banco.exception.NegocioException;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OperacionService {

    private static final int MAX_INTENTOS = 3;

    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;

    public OperacionService(CuentaRepository cuentaRepo, MovimientoRepository movRepo,
                            PasswordEncoder encoder, ApplicationEventPublisher publisher) {
        this.cuentaRepo = cuentaRepo;
        this.movRepo = movRepo;
        this.encoder = encoder;
        this.publisher = publisher;
    }

    // ---------------------------- DEPOSITO ----------------------------
    @Transactional
    public Movimiento depositar(String nro, BigDecimal monto) {
        validarMonto(monto);
        Cuenta c = bloquear(nro);
        validarActiva(c);
        Movimiento m = registrar(c, "D", monto, "Depósito en ventanilla", null);
        notificar(c, m, "Confirmación de depósito", "Depósito realizado", "Depósito");
        return m;
    }

    // ---------------------------- RETIRO ----------------------------
    /**
     * noRollbackFor: si la clave es incorrecta se lanza NegocioException, pero el contador de
     * intentos fallidos (y el posible bloqueo) debe guardarse igual.
     */
    @Transactional(noRollbackFor = NegocioException.class)
    public Movimiento retirar(String nro, BigDecimal monto, String clave) {
        validarMonto(monto);
        Cuenta c = bloquear(nro);
        validarActiva(c);
        validarClave(c, clave);
        validarSaldo(c, monto);
        Movimiento m = registrar(c, "R", monto, "Retiro en ventanilla", null);
        notificar(c, m, "Confirmación de retiro", "Retiro realizado", "Retiro");
        return m;
    }

    // ---------------------------- TRANSFERENCIA ----------------------------
    @Transactional(noRollbackFor = NegocioException.class)
    public Movimiento transferir(String origen, String destino, BigDecimal monto, String clave) {
        validarMonto(monto);
        if (origen.equals(destino)) {
            throw new NegocioException("La cuenta destino debe ser distinta de la cuenta origen");
        }
        // Se bloquean siempre en el mismo orden para evitar bloqueos mutuos (deadlock)
        boolean origenPrimero = origen.compareTo(destino) < 0;
        Cuenta primera = bloquear(origenPrimero ? origen : destino);
        Cuenta segunda = bloquear(origenPrimero ? destino : origen);
        Cuenta co = origenPrimero ? primera : segunda;
        Cuenta cd = origenPrimero ? segunda : primera;

        validarActiva(co);
        validarActiva(cd);
        if (!co.getTipoCuenta().getMoneda().equals(cd.getTipoCuenta().getMoneda())) {
            throw new NegocioException("Solo se permiten transferencias entre cuentas de la misma moneda");
        }
        validarClave(co, clave);
        validarSaldo(co, monto);

        String ref = "T" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Movimiento salida = registrar(co, "R", monto, "Transferencia enviada a la cuenta " + cd.getNroCta(), ref);
        Movimiento entrada = registrar(cd, "D", monto, "Transferencia recibida de la cuenta " + co.getNroCta(), ref);

        notificar(co, salida, "Confirmación de transferencia", "Transferencia enviada", "Transferencia enviada a " + cd.getNroCta());
        notificar(cd, entrada, "Confirmación de transferencia", "Transferencia recibida", "Transferencia recibida de " + co.getNroCta());
        return salida;
    }

    // ---------------------------- helpers ----------------------------

    private Cuenta bloquear(String nro) {
        return cuentaRepo.findByIdForUpdate(nro == null ? "" : nro.trim())
                .orElseThrow(() -> new NegocioException("La cuenta " + nro + " no existe"));
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new NegocioException("El monto debe ser mayor a 0");
        }
    }

    private void validarActiva(Cuenta c) {
        if (!"ACTIVA".equals(c.getEstado())) {
            throw new NegocioException("La cuenta " + c.getNroCta() + " está " + c.getEstado() + " y no puede operar");
        }
    }

    private void validarSaldo(Cuenta c, BigDecimal monto) {
        if (c.getSaldoCta().compareTo(monto) < 0) {
            throw new NegocioException("Saldo insuficiente. Saldo disponible: "
                    + Formato.dinero(c.getTipoCuenta().getSimbolo(), c.getSaldoCta()));
        }
    }

    private void validarClave(Cuenta c, String clave) {
        boolean ok = clave != null && c.getClave() != null && encoder.matches(clave, c.getClave());
        if (ok) {
            c.setIntentosFallidos(0);
            return;
        }
        c.setIntentosFallidos(c.getIntentosFallidos() + 1);
        if (c.getIntentosFallidos() >= MAX_INTENTOS) {
            c.setEstado("BLOQUEADA");
            throw new NegocioException("Clave incorrecta. La cuenta " + c.getNroCta()
                    + " fue BLOQUEADA por exceder " + MAX_INTENTOS + " intentos.");
        }
        throw new NegocioException("Clave de cuenta incorrecta. Intentos restantes: "
                + (MAX_INTENTOS - c.getIntentosFallidos()));
    }

    /** Registra el movimiento con saldo anterior y posterior y actualiza el saldo de la cuenta. */
    private Movimiento registrar(Cuenta c, String tipo, BigDecimal monto, String descripcion, String ref) {
        BigDecimal anterior = c.getSaldoCta();
        BigDecimal posterior = "D".equals(tipo) ? anterior.add(monto) : anterior.subtract(monto);
        int nroMov = movRepo.maxNroMov(c.getNroCta()) + 1;

        Movimiento m = new Movimiento();
        m.setId(new MovimientoId(c.getNroCta(), nroMov));
        m.setMontoMov(monto);
        m.setTipoMov(tipo);
        m.setDescripcion(descripcion);
        m.setFechaMov(LocalDateTime.now());
        m.setSaldoAnt(anterior);
        m.setSaldoPost(posterior);
        m.setRefTransf(ref);

        c.setSaldoCta(posterior);
        return movRepo.save(m);
    }

    private void notificar(Cuenta c, Movimiento m, String asunto, String titulo, String operacion) {
        String simbolo = c.getTipoCuenta().getSimbolo();
        for (Cliente t : c.getTitulares()) {
            Map<String, String> datos = new LinkedHashMap<>();
            datos.put("Cuenta", c.getNroCta());
            datos.put("Operación", operacion);
            datos.put("Monto", Formato.dinero(simbolo, m.getMontoMov()));
            datos.put("Saldo anterior", Formato.dinero(simbolo, m.getSaldoAnt()));
            datos.put("Saldo actual", Formato.dinero(simbolo, m.getSaldoPost()));
            datos.put("Fecha", Formato.fechaHora(m.getFechaMov()));
            publisher.publishEvent(new NotificacionEvent(t.getCliEmail(), asunto,
                    Correos.html(titulo, "Estimado(a) " + t.getCliNom() + ":", datos)));
        }
    }
}
