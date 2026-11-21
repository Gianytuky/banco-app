package com.banco.dto;

import java.math.BigDecimal;

public class CuentaResumen {

    private String nroCta;

    private String titulares;

    private String tipo;

    private String moneda;

    private String simbolo;

    private BigDecimal saldo;

    private String estado;

    public String getNroCta() { return nroCta; }
    public void setNroCta(String nroCta) { this.nroCta = nroCta; }

    public String getTitulares() { return titulares; }
    public void setTitulares(String titulares) { this.titulares = titulares; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
