package com.banco.dto;

import java.math.BigDecimal;

/** Proyeccion para las consultas nativas de reportes (alias: etiqueta, cantidad, monto). */
public interface FilaReporte {

    String getEtiqueta();

    Long getCantidad();

    BigDecimal getMonto();
}
