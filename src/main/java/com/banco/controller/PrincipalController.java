package com.banco.controller;

import com.banco.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalController {

    private final ReporteService reportes;

    public PrincipalController(ReporteService reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", reportes.clientes());
        model.addAttribute("totalCuentas", reportes.cuentas());
        model.addAttribute("totalMovimientos", reportes.movimientos());
        model.addAttribute("saldos", reportes.saldosPorMoneda());
        return "index";
    }
}
