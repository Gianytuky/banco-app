package com.banco.service;

import com.banco.dto.ClienteForm;
import com.banco.entity.Cliente;
import com.banco.event.NotificacionEvent;
import com.banco.exception.NegocioException;
import com.banco.repository.ClienteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final ApplicationEventPublisher publisher;

    public ClienteService(ClienteRepository repo, ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscar(String dni, String nombre) {
        String d = dni == null ? "" : dni.trim();
        String n = nombre == null ? "" : nombre.trim().toLowerCase();
        return repo.findAll(Sort.by("cliNom")).stream()
                .filter(c -> d.isEmpty() || c.getCliDni().contains(d))
                .filter(c -> n.isEmpty() || c.getCliNom().toLowerCase().contains(n))
                .toList();
    }

    @Transactional(readOnly = true)
    public Cliente porCodigo(String cod) {
        return repo.findById(cod).orElseThrow(() -> new NegocioException("El cliente " + cod + " no existe"));
    }

    @Transactional(readOnly = true)
    public Cliente porDni(String dni) {
        return repo.findByCliDni(dni).orElseThrow(() -> new NegocioException("No existe un cliente con DNI " + dni));
    }

    @Transactional
    public Cliente registrar(ClienteForm f) {
        if (repo.existsByCliDni(f.getDni())) {
            throw new NegocioException("Ya existe un cliente registrado con el DNI " + f.getDni());
        }
        Cliente c = new Cliente();
        c.setCliCod(siguienteCodigo());
        c.setCliDni(f.getDni());
        c.setCliNom(f.getNombre().trim().toUpperCase());
        c.setCliTel(vacioANull(f.getTelefono()));
        c.setCliRuc(vacioANull(f.getRuc()));
        c.setCliEmail(f.getEmail().trim());
        c.setCliDir(vacioANull(f.getDireccion()));
        c.setFechaRegistro(LocalDateTime.now());
        c = repo.save(c);

        Map<String, String> datos = new LinkedHashMap<>();
        datos.put("Código de cliente", c.getCliCod());
        datos.put("DNI", c.getCliDni());
        datos.put("Nombre", c.getCliNom());
        datos.put("Correo", c.getCliEmail());
        publisher.publishEvent(new NotificacionEvent(c.getCliEmail(), "Confirmación de registro",
                Correos.html("Registro de cliente", "Estimado(a) " + c.getCliNom() + ", su registro fue exitoso.", datos)));
        return c;
    }

    @Transactional
    public Cliente actualizar(String cod, ClienteForm f) {
        Cliente c = porCodigo(cod);
        // DNI y nombre provienen de RENIEC y no se modifican al editar
        c.setCliTel(vacioANull(f.getTelefono()));
        c.setCliRuc(vacioANull(f.getRuc()));
        c.setCliEmail(f.getEmail().trim());
        c.setCliDir(vacioANull(f.getDireccion()));
        return c;
    }

    private String siguienteCodigo() {
        String max = repo.maxCodigo();
        int n = (max == null) ? 0 : Integer.parseInt(max.substring(1));
        return String.format("C%04d", n + 1);
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
