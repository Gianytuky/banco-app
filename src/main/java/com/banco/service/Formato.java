package com.banco.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilidades de formato para los correos. */
public final class Formato {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formato() {
    }

    public static String dinero(String simbolo, BigDecimal monto) {
        return String.format(Locale.US, "%s %,.2f", simbolo, monto);
    }

    public static String fechaHora(LocalDateTime f) {
        return f == null ? "" : f.format(FECHA_HORA);
    }
}
