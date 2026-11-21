package com.banco.repository;

import com.banco.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByCliDni(String cliDni);

    boolean existsByCliDni(String cliDni);

    @Query("select max(c.cliCod) from Cliente c")
    String maxCodigo();
}
