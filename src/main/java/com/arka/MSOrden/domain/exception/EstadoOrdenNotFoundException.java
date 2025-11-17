package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un estado de orden.
 */
public class EstadoOrdenNotFoundException extends DomainException {

    public EstadoOrdenNotFoundException(Long estadoId) {
        super("Estado de orden con ID " + estadoId + " no encontrado");
    }

    public EstadoOrdenNotFoundException(String message) {
        super(message);
    }
}

