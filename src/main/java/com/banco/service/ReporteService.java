package com.banco.service;

import com.banco.dto.FilaReporte;
import com.banco.entity.Movimiento;
import com.banco.repository.ClienteRepository;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final ClienteRepository clienteRepo;
    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;

    public ReporteService(ClienteRepository clienteRepo, CuentaRepository cuentaRepo, MovimientoRepository movRepo) {
        this.clienteRepo = clienteRepo;
        this.cuentaRepo = cuentaRepo;
        this.movRepo = movRepo;
    }

    public long clientes() {
        return clienteRepo.count();
    }

    public long cuentas() {
        return cuentaRepo.count();
    }

    public long movimientos() {
        return movRepo.count();
    }

    public List<FilaReporte> depositos() {
        return movRepo.totalesPorMoneda("D");
    }

    public List<FilaReporte> retiros() {
        return movRepo.totalesPorMoneda("R");
    }

    public List<FilaReporte> cuentasPorTipo() {
        return cuentaRepo.cuentasPorTipo();
    }

    public List<FilaReporte> saldosPorMoneda() {
        return cuentaRepo.saldosPorMoneda();
    }

    /** Movimientos entre dos fechas (ambas incluidas). */
    public List<Movimiento> movimientosPorRango(LocalDate desde, LocalDate hasta) {
        return movRepo.porRango(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }
}
