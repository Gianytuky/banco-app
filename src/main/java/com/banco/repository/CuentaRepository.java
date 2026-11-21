package com.banco.repository;

import com.banco.dto.FilaReporte;
import com.banco.entity.Cuenta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {

    /** Bloquea la fila de la cuenta (SELECT ... FOR UPDATE) mientras dure la transaccion. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cuenta c where c.nroCta = :nro")
    Optional<Cuenta> findByIdForUpdate(@Param("nro") String nro);

    @Query("select distinct c from Cuenta c join c.titulares t where t.cliDni = :dni order by c.nroCta")
    List<Cuenta> findByTitularDni(@Param("dni") String dni);

    List<Cuenta> findAllByOrderByNroCtaAsc();

    @Query("select max(c.nroCta) from Cuenta c")
    String maxNroCta();

    @Transactional
    @Modifying
    @Query("update Cuenta c set c.clave = :clave where c.clave is null")
    int fijarClaveInicial(@Param("clave") String clave);

    @Query(value = "SELECT t.tip_nom AS etiqueta, COUNT(c.nro_cta) AS cantidad, COALESCE(SUM(c.saldo_cta),0) AS monto "
            + "FROM tipocuenta t LEFT JOIN cuenta c ON c.tip_cta = t.tip_cta "
            + "GROUP BY t.tip_cta, t.tip_nom ORDER BY t.tip_cta", nativeQuery = true)
    List<FilaReporte> cuentasPorTipo();

    @Query(value = "SELECT t.moneda AS etiqueta, COUNT(c.nro_cta) AS cantidad, COALESCE(SUM(c.saldo_cta),0) AS monto "
            + "FROM cuenta c JOIN tipocuenta t ON t.tip_cta = c.tip_cta "
            + "GROUP BY t.moneda ORDER BY t.moneda", nativeQuery = true)
    List<FilaReporte> saldosPorMoneda();
}
