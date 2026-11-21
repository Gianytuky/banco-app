package com.banco.controller;

import com.banco.dto.ClienteForm;
import com.banco.entity.Cliente;
import com.banco.exception.NegocioException;
import com.banco.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    // ----- Pagina 2: listado de clientes -----
    @GetMapping
    public String listar(@RequestParam(required = false) String dni,
                         @RequestParam(required = false) String nombre, Model model) {
        model.addAttribute("clientes", service.buscar(dni, nombre));
        model.addAttribute("dni", dni);
        model.addAttribute("nombre", nombre);
        return "clientes";
    }

    // ----- Pagina 3: registro de clientes -----
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new ClienteForm());
        preparar(model, true, "/clientes/nuevo");
        return "cliente-form";
    }

    @PostMapping("/nuevo")
    public String guardar(@Valid @ModelAttribute("form") ClienteForm form, BindingResult br,
                          Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            preparar(model, true, "/clientes/nuevo");
            return "cliente-form";
        }
        try {
            Cliente c = service.registrar(form);
            ra.addFlashAttribute("ok", "Cliente registrado: " + c.getCliNom() + " (" + c.getCliCod() + ")");
            return "redirect:/clientes?dni=" + c.getCliDni();
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            preparar(model, true, "/clientes/nuevo");
            return "cliente-form";
        }
    }

    // ----- Editar datos -----
    @GetMapping("/{cod}/editar")
    public String editar(@PathVariable String cod, Model model, RedirectAttributes ra) {
        try {
            Cliente c = service.porCodigo(cod);
            ClienteForm f = new ClienteForm();
            f.setDni(c.getCliDni());
            f.setNombre(c.getCliNom());
            f.setTelefono(c.getCliTel());
            f.setRuc(c.getCliRuc());
            f.setEmail(c.getCliEmail());
            f.setDireccion(c.getCliDir());
            model.addAttribute("form", f);
            model.addAttribute("cliCod", cod);
            preparar(model, false, "/clientes/" + cod + "/editar");
            return "cliente-form";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{cod}/editar")
    public String actualizar(@PathVariable String cod, @Valid @ModelAttribute("form") ClienteForm form,
                             BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("cliCod", cod);
            preparar(model, false, "/clientes/" + cod + "/editar");
            return "cliente-form";
        }
        try {
            Cliente c = service.actualizar(cod, form);
            ra.addFlashAttribute("ok", "Datos actualizados: " + c.getCliNom());
            return "redirect:/clientes?dni=" + c.getCliDni();
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliCod", cod);
            preparar(model, false, "/clientes/" + cod + "/editar");
            return "cliente-form";
        }
    }

    private void preparar(Model model, boolean nuevo, String accion) {
        model.addAttribute("nuevo", nuevo);
        model.addAttribute("accion", accion);
    }
}
