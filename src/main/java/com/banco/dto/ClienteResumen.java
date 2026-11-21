package com.banco.dto;


public class ClienteResumen {

    private String cliCod;

    private String dni;

    private String nombre;

    private String email;

    public String getCliCod() { return cliCod; }
    public void setCliCod(String cliCod) { this.cliCod = cliCod; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public ClienteResumen() {
    }

    public ClienteResumen(String cliCod, String dni, String nombre, String email) {
        this.cliCod = cliCod;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
    }

}
