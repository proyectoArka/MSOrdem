package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando no hay suficiente stock de un producto.
 */
public class StockInsuficienteException extends DomainException {

    public StockInsuficienteException(Long productoId, Long stockDisponible, Long cantidadSolicitada) {
        super(String.format("Stock insuficiente para el producto ID %d. Disponible: %d, Solicitado: %d",
                productoId, stockDisponible, cantidadSolicitada));
    }

    public StockInsuficienteException(String message) {
        super(message);
    }
}

