package com.banco.controller;

import com.banco.dto.AperturaForm;
import com.banco.entity.Cuenta;
import com.banco.exception.NegocioException;
import com.banco.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    // ----- Pagina 5: consulta de cuentas -----
    @GetMapping
    public String consultar(@RequestParam(required = false) String dni,
                            @RequestParam(required = false) String nro, Model model) {
        List<Cuenta> cuentas;
        if (nro != null && !nro.isBlank()) {
            cuentas = service.porNro(nro).map(c -> List.of(c)).orElse(List.of());
        } else if (dni != null && !dni.isBlank()) {
            cuentas = service.porDni(dni);
        } else {
            cuentas = service.todas();
        }
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("dni", dni);
        model.addAttribute("nro", nro);
        return "cuentas";
    }

    // ----- Pagina 4: apertura de cuenta -----
    @GetMapping("/apertura")
    public String apertura(@RequestParam(required = false) String dni, Model model) {
        AperturaForm form = new AperturaForm();
        if (dni != null) {
            form.setDni(dni);
        }
        model.addAttribute("form", form);
        model.addAttribute("tipos", service.tipos());
        return "apertura-cuenta";
    }

    @PostMapping("/apertura")
    public String aperturar(@Valid @ModelAttribute("form") AperturaForm form, BindingResult br,
                            Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("tipos", service.tipos());
            return "apertura-cuenta";
        }
        try {
            Cuenta c = service.aperturar(form);
            ra.addFlashAttribute("ok", "Cuenta " + c.getNroCta() + " aperturada con un depósito inicial de "
                    + c.getTipoCuenta().getSimbolo() + " " + form.getMonto());
            return "redirect:/cuentas?nro=" + c.getNroCta();
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tipos", service.tipos());
            return "apertura-cuenta";
        }
    }
}
