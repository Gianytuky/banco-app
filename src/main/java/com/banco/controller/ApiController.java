package com.banco.controller;

import com.banco.dto.ClienteResumen;
import com.banco.dto.CuentaResumen;
import com.banco.dto.DniDTO;
import com.banco.entity.Cliente;
import com.banco.exception.NegocioException;
import com.banco.service.ClienteService;
import com.banco.service.CuentaService;
import com.banco.service.DniService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * API REST interna (JSON). La usan los botones "Consultar DNI", "Buscar cliente" y "Buscar cuenta"
 * de las paginas mediante fetch, sin recargar la pagina. Requiere sesion iniciada (Spring Security).
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final DniService dniService;
    private final ClienteService clienteService;
    private final CuentaService cuentaService;

    public ApiController(DniService dniService, ClienteService clienteService, CuentaService cuentaService) {
        this.dniService = dniService;
        this.clienteService = clienteService;
        this.cuentaService = cuentaService;
    }

    /** Consulta la API externa de DNI. */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {
        try {
            DniDTO dto = dniService.consultar(dni);
            return ResponseEntity.ok(dto);
        } catch (NegocioException e) {
            return error(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    /** Busca un cliente ya registrado por DNI. */
    @GetMapping("/clientes/{dni}")
    public ResponseEntity<?> cliente(@PathVariable String dni) {
        try {
            Cliente c = clienteService.porDni(dni);
            return ResponseEntity.ok(new ClienteResumen(c.getCliCod(), c.getCliDni(), c.getCliNom(), c.getCliEmail()));
        } catch (NegocioException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /** Resumen de una cuenta (titulares, tipo, moneda, saldo, estado). */
    @GetMapping("/cuentas/{nro}")
    public ResponseEntity<?> cuenta(@PathVariable String nro) {
        try {
            CuentaResumen r = cuentaService.resumen(nro);
            return ResponseEntity.ok(r);
        } catch (NegocioException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("mensaje", mensaje));
    }
}
