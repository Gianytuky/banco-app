package com.banco.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AperturaForm {

    @NotBlank(message = "Ingrese el DNI del cliente")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotNull(message = "Seleccione el tipo de cuenta")
    private Integer tipCta;

    @NotNull(message = "Ingrese el depósito inicial")
    @DecimalMin(value = "0.01", message = "El depósito inicial debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "Ingrese la clave")
    @Pattern(regexp = "\\d{4}", message = "La clave debe tener 4 dígitos")
    private String clave;

    @NotBlank(message = "Confirme la clave")
    private String confirmarClave;

    private List<String> titularesDni = new ArrayList<>();

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public Integer getTipCta() { return tipCta; }
    public void setTipCta(Integer tipCta) { this.tipCta = tipCta; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getConfirmarClave() { return confirmarClave; }
    public void setConfirmarClave(String confirmarClave) { this.confirmarClave = confirmarClave; }

    public List<String> getTitularesDni() { return titularesDni; }
    public void setTitularesDni(List<String> titularesDni) { this.titularesDni = titularesDni; }

}
