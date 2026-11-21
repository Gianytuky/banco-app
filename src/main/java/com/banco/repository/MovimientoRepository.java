package com.banco.repository;

import com.banco.dto.FilaReporte;
import com.banco.entity.Movimiento;
import com.banco.entity.MovimientoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, MovimientoId> {

    List<Movimiento> findByIdNroCtaOrderByIdNroMovAsc(String nroCta);

    @Query("select coalesce(max(m.id.nroMov), 0) from Movimiento m where m.id.nroCta = :nro")
    int maxNroMov(@Param("nro") String nro);

    @Query("select m from Movimiento m where m.fechaMov >= :desde and m.fechaMov < :hasta "
            + "order by m.fechaMov desc, m.id.nroCta, m.id.nroMov desc")
    List<Movimiento> porRango(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /** Total de movimientos de un tipo (D o R) agrupado por moneda de la cuenta. */
    @Query(value = "SELECT t.moneda AS etiqueta, COUNT(*) AS cantidad, COALESCE(SUM(m.monto_mov),0) AS monto "
            + "FROM movimiento m JOIN cuenta c ON c.nro_cta = m.nro_cta "
            + "JOIN tipocuenta t ON t.tip_cta = c.tip_cta "
            + "WHERE m.tipo_mov = :tipo GROUP BY t.moneda ORDER BY t.moneda", nativeQuery = true)
    List<FilaReporte> totalesPorMoneda(@Param("tipo") String tipo);
}
