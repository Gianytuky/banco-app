package com.banco.controller;

import com.banco.entity.Cuenta;
import com.banco.entity.Movimiento;
import com.banco.service.CuentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private final CuentaService cuentaService;

    public MovimientoController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    // ----- Pagina 8: consulta de movimientos -----
    @GetMapping
    public String ver(@RequestParam(required = false) String nro, Model model) {
        model.addAttribute("nro", nro);
        if (nro != null && !nro.isBlank()) {
            Optional<Cuenta> cuenta = cuentaService.porNro(nro);
            if (cuenta.isPresent()) {
                List<Movimiento> movs = cuentaService.movimientos(cuenta.get().getNroCta());
                BigDecimal depositos = movs.stream().filter(m -> "D".equals(m.getTipoMov()))
                        .map(Movimiento::getMontoMov).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal retiros = movs.stream().filter(m -> "R".equals(m.getTipoMov()))
                        .map(Movimiento::getMontoMov).reduce(BigDecimal.ZERO, BigDecimal::add);
                model.addAttribute("cuenta", cuenta.get());
                model.addAttribute("movs", movs);
                model.addAttribute("totalDepositos", depositos);
                model.addAttribute("totalRetiros", retiros);
            } else {
                model.addAttribute("error", "La cuenta " + nro + " no existe");
            }
        }
        return "movimientos";
    }
}
