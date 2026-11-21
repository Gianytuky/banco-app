package com.banco.repository;

import com.banco.entity.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCuentaRepository extends JpaRepository<TipoCuenta, Integer> {
}
