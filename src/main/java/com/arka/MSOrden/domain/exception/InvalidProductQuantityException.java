package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando la cantidad de un producto es inválida.
 */
public class InvalidProductQuantityException extends DomainException {

    public InvalidProductQuantityException(Long productoId, Long cantidad) {
        super(String.format("Cantidad inválida (%d) para el producto con ID %d. La cantidad debe ser mayor a 0",
                cantidad, productoId));
    }

    public InvalidProductQuantityException(String message) {
        super(message);
    }
}

