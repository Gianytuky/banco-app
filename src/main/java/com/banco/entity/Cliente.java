package com.banco.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(name = "cli_cod", length = 5)
    private String cliCod;

    @Column(name = "cli_dni", length = 8, nullable = false, unique = true)
    private String cliDni;

    @Column(name = "cli_nom", nullable = false)
    private String cliNom;

    @Column(name = "cli_tel")
    private String cliTel;

    @Column(name = "cli_ruc")
    private String cliRuc;

    @Column(name = "cli_email", nullable = false)
    private String cliEmail;

    @Column(name = "cli_dir")
    private String cliDir;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public String getCliCod() { return cliCod; }
    public void setCliCod(String cliCod) { this.cliCod = cliCod; }

    public String getCliDni() { return cliDni; }
    public void setCliDni(String cliDni) { this.cliDni = cliDni; }

    public String getCliNom() { return cliNom; }
    public void setCliNom(String cliNom) { this.cliNom = cliNom; }

    public String getCliTel() { return cliTel; }
    public void setCliTel(String cliTel) { this.cliTel = cliTel; }

    public String getCliRuc() { return cliRuc; }
    public void setCliRuc(String cliRuc) { this.cliRuc = cliRuc; }

    public String getCliEmail() { return cliEmail; }
    public void setCliEmail(String cliEmail) { this.cliEmail = cliEmail; }

    public String getCliDir() { return cliDir; }
    public void setCliDir(String cliDir) { this.cliDir = cliDir; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }


    @Override
    public boolean equals(Object o) {
        return o instanceof Cliente c && Objects.equals(getCliCod(), c.getCliCod());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCliCod());
    }

}
