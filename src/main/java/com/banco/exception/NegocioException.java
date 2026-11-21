package com.banco.exception;

/** Error de regla de negocio (mensaje apto para mostrar al usuario). */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
