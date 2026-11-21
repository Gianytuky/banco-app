package com.banco.dto;

import jakarta.validation.constraints.*;

public class ClienteForm {

    @NotBlank(message = "Ingrese el DNI")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "Consulte el DNI o ingrese el nombre completo")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @Pattern(regexp = "^\\d{0,15}$", message = "Solo dígitos (máximo 15)")
    private String telefono;

    @Pattern(regexp = "^(\\d{11})?$", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @NotBlank(message = "Ingrese el correo")
    @Email(message = "Correo no válido")
    @Size(max = 120)
    private String email;

    @Size(max = 100, message = "Máximo 100 caracteres")
    private String direccion;

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

}
