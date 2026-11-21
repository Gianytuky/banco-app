package com.banco.event;

/** Evento que se publica dentro de la transaccion y se envia por correo DESPUES del commit. */
public record NotificacionEvent(String para, String asunto, String html) {
}
