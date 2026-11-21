package com.banco.controller;

import com.banco.exception.NegocioException;
import com.banco.service.OperacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/operaciones")
public class OperacionController {

    private final OperacionService service;

    public OperacionController(OperacionService service) {
        this.service = service;
    }

    // ----- Pagina 6: depositos -----
    @GetMapping("/deposito")
    public String deposito(@RequestParam(required = false) String nro, Model model) {
        model.addAttribute("nro", nro);
        return "deposito";
    }

    @PostMapping("/deposito")
    public String depositar(@RequestParam String nro, @RequestParam BigDecimal monto,
                            Model model, RedirectAttributes ra) {
        try {
            service.depositar(nro, monto);
            ra.addFlashAttribute("ok", "Depósito registrado correctamente en la cuenta " + nro);
            return "redirect:/movimientos?nro=" + nro;
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nro", nro);
            model.addAttribute("monto", monto);
            return "deposito";
        }
    }

    // ----- Pagina 7: retiros -----
    @GetMapping("/retiro")
    public String retiro(@RequestParam(required = false) String nro, Model model) {
        model.addAttribute("nro", nro);
        return "retiro";
    }

    @PostMapping("/retiro")
    public String retirar(@RequestParam String nro, @RequestParam BigDecimal monto, @RequestParam String clave,
                          Model model, RedirectAttributes ra) {
        try {
            service.retirar(nro, monto, clave);
            ra.addFlashAttribute("ok", "Retiro registrado correctamente en la cuenta " + nro);
            return "redirect:/movimientos?nro=" + nro;
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nro", nro);
            model.addAttribute("monto", monto);
            return "retiro";
        }
    }

    // ----- Transferencias -----
    @GetMapping("/transferencia")
    public String transferencia(@RequestParam(required = false) String nro, Model model) {
        model.addAttribute("nro", nro);
        return "transferencia";
    }

    @PostMapping("/transferencia")
    public String transferir(@RequestParam String nro, @RequestParam String destino, @RequestParam BigDecimal monto,
                             @RequestParam String clave, Model model, RedirectAttributes ra) {
        try {
            service.transferir(nro, destino, monto, clave);
            ra.addFlashAttribute("ok", "Transferencia realizada de la cuenta " + nro + " a la cuenta " + destino);
            return "redirect:/movimientos?nro=" + nro;
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nro", nro);
            model.addAttribute("destino", destino);
            model.addAttribute("monto", monto);
            return "transferencia";
        }
    }
}
