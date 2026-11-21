package com.banco.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovimientoId implements Serializable {

    @Column(name = "nro_cta", length = 6)
    private String nroCta;

    @Column(name = "nro_mov")
    private int nroMov;

    public String getNroCta() { return nroCta; }
    public void setNroCta(String nroCta) { this.nroCta = nroCta; }

    public int getNroMov() { return nroMov; }
    public void setNroMov(int nroMov) { this.nroMov = nroMov; }


    public MovimientoId() {
    }

    public MovimientoId(String nroCta, int nroMov) {
        this.nroCta = nroCta;
        this.nroMov = nroMov;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MovimientoId m && nroMov == m.nroMov && Objects.equals(nroCta, m.nroCta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nroCta, nroMov);
    }

}
