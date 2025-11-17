package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una orden.
 */
public class OrdenNotFoundException extends DomainException {

    public OrdenNotFoundException(Long ordenId) {
        super("Orden con ID " + ordenId + " no encontrada");
    }

    public OrdenNotFoundException(String message) {
        super(message);
    }
}

