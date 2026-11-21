package com.banco.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
public class Movimiento {

    @EmbeddedId
    private MovimientoId id;

    @Column(name = "monto_mov", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMov;

    @Column(name = "tipo_mov", nullable = false, length = 1)
    private String tipoMov;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_mov")
    private LocalDateTime fechaMov;

    @Column(name = "saldo_ant", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoAnt;

    @Column(name = "saldo_post", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoPost;

    @Column(name = "ref_transf", length = 20)
    private String refTransf;

    public MovimientoId getId() { return id; }
    public void setId(MovimientoId id) { this.id = id; }

    public BigDecimal getMontoMov() { return montoMov; }
    public void setMontoMov(BigDecimal montoMov) { this.montoMov = montoMov; }

    public String getTipoMov() { return tipoMov; }
    public void setTipoMov(String tipoMov) { this.tipoMov = tipoMov; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaMov() { return fechaMov; }
    public void setFechaMov(LocalDateTime fechaMov) { this.fechaMov = fechaMov; }

    public BigDecimal getSaldoAnt() { return saldoAnt; }
    public void setSaldoAnt(BigDecimal saldoAnt) { this.saldoAnt = saldoAnt; }

    public BigDecimal getSaldoPost() { return saldoPost; }
    public void setSaldoPost(BigDecimal saldoPost) { this.saldoPost = saldoPost; }

    public String getRefTransf() { return refTransf; }
    public void setRefTransf(String refTransf) { this.refTransf = refTransf; }

}
