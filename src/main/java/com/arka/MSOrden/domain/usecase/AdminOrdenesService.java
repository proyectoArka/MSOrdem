package com.arka.MSOrden.domain.usecase;

import com.arka.MSOrden.application.dtos.DetalleOrdenDto;
import com.arka.MSOrden.application.dtos.DetalleProductoOrdenDto;
import com.arka.MSOrden.application.dtos.ListarOrdenDto;
import com.arka.MSOrden.domain.model.Gateway.AuthGateway;
import com.arka.MSOrden.domain.model.Gateway.EstadoOrdenGateway;
import com.arka.MSOrden.domain.model.Gateway.InventarioGateway;
import com.arka.MSOrden.domain.model.Gateway.OrdenGateway;
import com.arka.MSOrden.domain.model.Gateway.OrdenProductoGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AdminOrdenesService {

    private final OrdenGateway ordenGateway;
    private final EstadoOrdenGateway estadoOrdenGateway;
    private final OrdenProductoGateway ordenProductoGateway;
    private final AuthGateway authGateway;
    private final InventarioGateway inventarioGateway;

    public AdminOrdenesService(OrdenGateway ordenGateway,
                               EstadoOrdenGateway estadoOrdenGateway,
                               OrdenProductoGateway ordenProductoGateway,
                               AuthGateway authGateway,
                               InventarioGateway inventarioGateway) {
        this.ordenGateway = ordenGateway;
        this.estadoOrdenGateway = estadoOrdenGateway;
        this.ordenProductoGateway = ordenProductoGateway;
        this.authGateway = authGateway;
        this.inventarioGateway = inventarioGateway;
    }

    /**
     * Obtiene todas las órdenes con información completa
     * Incluye: id orden, id usuario, nombre usuario, fechas, totales y estado
     */
    public Flux<ListarOrdenDto> obtenerTodasLasOrdenes() {
        return ordenGateway.findAll()
                .flatMap(orden ->
                        // Obtener información del usuario
                        authGateway.consultarUsuario(orden.getUserId())
                                .flatMap(usuario ->
                                        // Obtener el estado de la orden
                                        estadoOrdenGateway.buscarEstadoPorId(orden.getEstadoorden())
                                                .map(estado -> ListarOrdenDto.builder()
                                                        .idOrden(orden.getId())
                                                        .idUsuario(orden.getUserId())
                                                        .nombreUsuario(usuario.getName())
                                                        .fechaCreacion(orden.getCreationOrden())
                                                        .fechaUltimoMovimiento(orden.getFechaUltimaModificacion())
                                                        .totalPrecio(orden.getTotalPedido())
                                                        .totalProductos(orden.getTotalDeProductos())
                                                        .totalUnidades(orden.getTotalUnidades())
                                                        .estadoOrden(estado.getNombreEstado())
                                                        .build()
                                                )
                                )
                                // Si falla la consulta del usuario o estado, crear DTO con valores por defecto
                                .onErrorResume(error -> Mono.just(ListarOrdenDto.builder()
                                        .idOrden(orden.getId())
                                        .idUsuario(orden.getUserId())
                                        .nombreUsuario("Usuario no encontrado")
                                        .fechaCreacion(orden.getCreationOrden())
                                        .fechaUltimoMovimiento(orden.getFechaUltimaModificacion())
                                        .totalPrecio(orden.getTotalPedido())
                                        .totalProductos(orden.getTotalDeProductos())
                                        .totalUnidades(orden.getTotalUnidades())
                                        .estadoOrden("Estado no disponible")
                                        .build()
                                ))
                );
    }

    /**
     * Obtiene el detalle de una orden específica por su ID
     * Incluye: id orden, precio total, cantidad de productos, total unidades y array de productos
     */
    public Mono<DetalleOrdenDto> obtenerDetalleOrden(Long ordenId) {
        return ordenGateway.findById(ordenId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada")))
                .flatMap(orden ->
                        // Obtener los productos de la orden
                        ordenProductoGateway.findByOrdenId(ordenId)
                                .flatMap(ordenProducto ->
                                        // Consultar información del producto en inventario
                                        inventarioGateway.consultarProducto(ordenProducto.getProductoId())
                                                .map(productoInventario -> DetalleProductoOrdenDto.builder()
                                                        .idProducto(ordenProducto.getProductoId())
                                                        .nombreProducto(productoInventario.getNombre())
                                                        .descripcionProducto(productoInventario.getDescripcion())
                                                        .cantidad(ordenProducto.getCantidad().intValue())
                                                        .precioUnidad(productoInventario.getPrice())
                                                        .precioTotal(ordenProducto.getPrecioTotal())
                                                        .build()
                                                )
                                                .onErrorResume(error ->
                                                        // Si falla la consulta al inventario, usar valores por defecto
                                                        Mono.just(DetalleProductoOrdenDto.builder()
                                                                .idProducto(ordenProducto.getProductoId())
                                                                .nombreProducto("Producto no disponible")
                                                                .descripcionProducto("Descripción no disponible")
                                                                .cantidad(ordenProducto.getCantidad().intValue())
                                                                .precioUnidad(0.0)
                                                                .precioTotal(ordenProducto.getPrecioTotal())
                                                                .build()
                                                        )
                                                )
                                )
                                .collectList()
                                .map(productos -> DetalleOrdenDto.builder()
                                        .idOrden(orden.getId())
                                        .precioTotal(orden.getTotalPedido())
                                        .cantidadProductos(orden.getTotalDeProductos())
                                        .totalUnidades(orden.getTotalUnidades())
                                        .productos(productos)
                                        .build()
                                )
                );
    }
}
