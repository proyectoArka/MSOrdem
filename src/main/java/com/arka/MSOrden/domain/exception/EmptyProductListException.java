package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear/actualizar una orden sin productos.
 */
public class EmptyProductListException extends DomainException {

    public EmptyProductListException() {
        super("No se pueden procesar órdenes sin productos. La lista de productos está vacía");
    }

    public EmptyProductListException(String message) {
        super(message);
    }
}

