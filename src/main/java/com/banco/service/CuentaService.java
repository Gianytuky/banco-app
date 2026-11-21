package com.banco.service;

import com.banco.dto.AperturaForm;
import com.banco.dto.CuentaResumen;
import com.banco.entity.Cliente;
import com.banco.entity.Cuenta;
import com.banco.entity.Movimiento;
import com.banco.entity.MovimientoId;
import com.banco.entity.TipoCuenta;
import com.banco.event.NotificacionEvent;
import com.banco.exception.NegocioException;
import com.banco.repository.ClienteRepository;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import com.banco.repository.TipoCuentaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CuentaService {

    private final ClienteRepository clienteRepo;
    private final TipoCuentaRepository tipoRepo;
    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;

    public CuentaService(ClienteRepository clienteRepo, TipoCuentaRepository tipoRepo, CuentaRepository cuentaRepo,
                         MovimientoRepository movRepo, PasswordEncoder encoder, ApplicationEventPublisher publisher) {
        this.clienteRepo = clienteRepo;
        this.tipoRepo = tipoRepo;
        this.cuentaRepo = cuentaRepo;
        this.movRepo = movRepo;
        this.encoder = encoder;
        this.publisher = publisher;
    }

    /**
     * REGLA DE NEGOCIO: apertura + primer deposito en UNA sola transaccion.
     * Si algo falla (por ejemplo al registrar el movimiento) se revierte tambien la cuenta.
     */
    @Transactional(rollbackFor = Exception.class)
    public Cuenta aperturar(AperturaForm f) {
        if (!f.getClave().equals(f.getConfirmarClave())) {
            throw new NegocioException("La clave y su confirmación no coinciden");
        }
        Cliente principal = clienteRepo.findByCliDni(f.getDni())
                .orElseThrow(() -> new NegocioException("No existe un cliente con DNI " + f.getDni()));
        TipoCuenta tipo = tipoRepo.findById(f.getTipCta())
                .orElseThrow(() -> new NegocioException("Tipo de cuenta no válido"));
        BigDecimal monto = f.getMonto();
        if (monto == null || monto.signum() <= 0) {
            throw new NegocioException("El depósito inicial debe ser mayor a 0");
        }

        // Titulares: el principal + los adicionales (solo si la cuenta es mancomunada)
        Set<Cliente> titulares = new LinkedHashSet<>();
        titulares.add(principal);
        if (tipo.isMancomunada()) {
            for (String dni : f.getTitularesDni()) {
                if (dni == null || dni.isBlank()) {
                    continue;
                }
                Cliente t = clienteRepo.findByCliDni(dni.trim())
                        .orElseThrow(() -> new NegocioException("No existe un cliente con DNI " + dni.trim()));
                titulares.add(t);
            }
            if (titulares.size() < 2) {
                throw new NegocioException("La cuenta mancomunada requiere al menos 2 titulares distintos");
            }
        }

        // 1) Registrar la cuenta (saldo en 0)
        Cuenta cuenta = new Cuenta();
        cuenta.setNroCta(siguienteNroCuenta());
        cuenta.setCliente(principal);
        cuenta.setTipoCuenta(tipo);
        cuenta.setSaldoCta(BigDecimal.ZERO);
        cuenta.setClave(encoder.encode(f.getClave()));
        cuenta.setEstado("ACTIVA");
        cuenta.setFechaApertura(LocalDateTime.now());
        cuenta.setIntentosFallidos(0);
        cuenta.setTitulares(titulares);
        cuenta = cuentaRepo.save(cuenta);

        // 2) Registrar el primer movimiento: DEPOSITO (saldo anterior 0, saldo posterior = monto)
        Movimiento mov = new Movimiento();
        mov.setId(new MovimientoId(cuenta.getNroCta(), 1));
        mov.setMontoMov(monto);
        mov.setTipoMov("D");
        mov.setDescripcion("Depósito por apertura de cuenta");
        mov.setFechaMov(LocalDateTime.now());
        mov.setSaldoAnt(BigDecimal.ZERO);
        mov.setSaldoPost(monto);
        movRepo.save(mov);

        // 3) Actualizar el saldo (se confirma todo junto al terminar el metodo)
        cuenta.setSaldoCta(monto);

        // 4) Correos (se envian solo si la transaccion hace commit)
        for (Cliente t : titulares) {
            Map<String, String> datos = new LinkedHashMap<>();
            datos.put("Número de cuenta", cuenta.getNroCta());
            datos.put("Tipo de cuenta", tipo.getTipNom() + " (" + tipo.getMoneda() + ")");
            datos.put("Titulares", cuenta.getTitularesTexto());
            datos.put("Depósito inicial", Formato.dinero(tipo.getSimbolo(), monto));
            datos.put("Saldo actual", Formato.dinero(tipo.getSimbolo(), monto));
            datos.put("Fecha", Formato.fechaHora(cuenta.getFechaApertura()));
            publisher.publishEvent(new NotificacionEvent(t.getCliEmail(), "Confirmación de apertura de cuenta",
                    Correos.html("Apertura de cuenta", "Estimado(a) " + t.getCliNom() + ", su cuenta fue aperturada.", datos)));
        }
        return cuenta;
    }

    private String siguienteNroCuenta() {
        String max = cuentaRepo.maxNroCta();
        long n = (max == null) ? 100000L : Long.parseLong(max);
        return String.valueOf(n + 1);
    }

    // ------------------------- consultas -------------------------

    @Transactional(readOnly = true)
    public List<TipoCuenta> tipos() {
        return tipoRepo.findAll(Sort.by("tipCta"));
    }

    @Transactional(readOnly = true)
    public List<Cuenta> todas() {
        return cuentaRepo.findAllByOrderByNroCtaAsc();
    }

    @Transactional(readOnly = true)
    public List<Cuenta> porDni(String dni) {
        return cuentaRepo.findByTitularDni(dni.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Cuenta> porNro(String nro) {
        return cuentaRepo.findById(nro.trim());
    }

    @Transactional(readOnly = true)
    public List<Movimiento> movimientos(String nro) {
        return movRepo.findByIdNroCtaOrderByIdNroMovAsc(nro);
    }

    @Transactional(readOnly = true)
    public CuentaResumen resumen(String nro) {
        Cuenta c = porNro(nro).orElseThrow(() -> new NegocioException("La cuenta " + nro + " no existe"));
        CuentaResumen r = new CuentaResumen();
        r.setNroCta(c.getNroCta());
        r.setTitulares(c.getTitularesTexto());
        r.setTipo(c.getTipoCuenta().getTipNom());
        r.setMoneda(c.getTipoCuenta().getMoneda());
        r.setSimbolo(c.getTipoCuenta().getSimbolo());
        r.setSaldo(c.getSaldoCta());
        r.setEstado(c.getEstado());
        return r;
    }
}
