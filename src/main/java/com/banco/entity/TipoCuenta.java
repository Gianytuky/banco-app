package com.banco.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipocuenta")
public class TipoCuenta {

    @Id
    @Column(name = "tip_cta")
    private Integer tipCta;

    @Column(name = "tip_nom", nullable = false)
    private String tipNom;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "mancomunada", nullable = false)
    private boolean mancomunada;

    public Integer getTipCta() { return tipCta; }
    public void setTipCta(Integer tipCta) { this.tipCta = tipCta; }

    public String getTipNom() { return tipNom; }
    public void setTipNom(String tipNom) { this.tipNom = tipNom; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public boolean isMancomunada() { return mancomunada; }
    public void setMancomunada(boolean mancomunada) { this.mancomunada = mancomunada; }


    /** Simbolo de la moneda para mostrar en pantalla y correos. */
    public String getSimbolo() {
        return "USD".equals(moneda) ? "US$" : "S/";
    }

}
