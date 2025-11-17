package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un producto en el inventario.
 */
public class ProductoNotFoundException extends DomainException {

    public ProductoNotFoundException(Long productoId) {
        super("Producto con ID " + productoId + " no encontrado");
    }

    public ProductoNotFoundException(String message) {
        super(message);
    }
}

