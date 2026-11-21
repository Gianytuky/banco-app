package com.banco.controller;

import com.banco.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    // ----- Pagina 9: reportes bancarios -----
    @GetMapping
    public String reportes(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                           Model model) {
        model.addAttribute("totalClientes", service.clientes());
        model.addAttribute("totalCuentas", service.cuentas());
        model.addAttribute("depositos", service.depositos());
        model.addAttribute("retiros", service.retiros());
        model.addAttribute("porTipo", service.cuentasPorTipo());
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        if (desde != null && hasta != null) {
            if (hasta.isBefore(desde)) {
                model.addAttribute("error", "La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
            } else {
                model.addAttribute("movs", service.movimientosPorRango(desde, hasta));
            }
        }
        return "reportes";
    }
}
