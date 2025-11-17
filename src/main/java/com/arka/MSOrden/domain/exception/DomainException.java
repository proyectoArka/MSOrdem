package com.arka.MSOrden.domain.exception;

/**
 * Excepción base del dominio.
 * Todas las excepciones de negocio deben extender de esta clase.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

