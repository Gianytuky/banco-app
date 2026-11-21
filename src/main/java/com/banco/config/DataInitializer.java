package com.banco.config;

import com.banco.entity.Usuario;
import com.banco.repository.CuentaRepository;
import com.banco.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Se ejecuta al iniciar la aplicacion:
 *  - crea el usuario admin (admin / admin123) si no existe
 *  - asigna la clave de cuenta 1234 (BCrypt) a las cuentas de prueba que tienen clave NULL
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepo;
    private final CuentaRepository cuentaRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UsuarioRepository usuarioRepo, CuentaRepository cuentaRepo, PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.cuentaRepo = cuentaRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepo.findByUsername("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername("admin");
            u.setPassword(encoder.encode("admin123"));
            u.setNombre("Administrador");
            u.setRol("ADMIN");
            u.setEstado("ACTIVO");
            usuarioRepo.save(u);
            log.info("Usuario admin creado (admin / admin123)");
        }
        int n = cuentaRepo.fijarClaveInicial(encoder.encode("1234"));
        if (n > 0) {
            log.info("Se asigno la clave inicial 1234 a {} cuentas de prueba", n);
        }
    }
}
