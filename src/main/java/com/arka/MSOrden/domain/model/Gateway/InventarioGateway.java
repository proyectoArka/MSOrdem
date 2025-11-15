package com.arka.MSOrden.domain.model.Gateway;

import com.arka.MSOrden.application.dtos.ConsultProductInventarioDto;
import reactor.core.publisher.Mono;

/**
 * Gateway para comunicación con el microservicio de Inventario.
 * Define el contrato de negocio sin depender de implementación técnica.
 */
public interface InventarioGateway {

    /**
     * Consulta información de un producto en el inventario
     * @param productoId ID del producto
     * @return Información del producto incluyendo stock y precio
     */
    Mono<ConsultProductInventarioDto> consultarProducto(Long productoId);

    /**
     * Actualiza el stock de un producto en el inventario
     * @param productoId ID del producto
     * @param nuevoStock Nuevo valor de stock
     * @return Mono vacío indicando éxito
     */
    Mono<Void> actualizarStock(Long productoId, Long nuevoStock);
}

