package com.banco.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    @Column(name = "nro_cta", length = 6)
    private String nroCta;

    @ManyToOne
    @JoinColumn(name = "cli_cod", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tip_cta", nullable = false)
    private TipoCuenta tipoCuenta;

    @Column(name = "saldo_cta", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoCta;

    @Column(name = "clave")
    private String clave;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @ManyToMany
    @JoinTable(name = "titular",
            joinColumns = @JoinColumn(name = "nro_cta"),
            inverseJoinColumns = @JoinColumn(name = "cli_cod"))
    private Set<Cliente> titulares = new LinkedHashSet<>();

    public String getNroCta() { return nroCta; }
    public void setNroCta(String nroCta) { this.nroCta = nroCta; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(TipoCuenta tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public BigDecimal getSaldoCta() { return saldoCta; }
    public void setSaldoCta(BigDecimal saldoCta) { this.saldoCta = saldoCta; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    public Set<Cliente> getTitulares() { return titulares; }
    public void setTitulares(Set<Cliente> titulares) { this.titulares = titulares; }


    /** Nombres de todos los titulares separados por coma. */
    public String getTitularesTexto() {
        return titulares.stream().map(Cliente::getCliNom).sorted().collect(Collectors.joining(", "));
    }

}
