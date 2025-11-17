package com.arka.MSOrden.domain.usecase;

import com.arka.MSOrden.application.dto.*;
import com.arka.MSOrden.domain.exception.EmptyProductListException;
import com.arka.MSOrden.domain.exception.InvalidProductQuantityException;
import com.arka.MSOrden.domain.exception.OrdenNotFoundException;
import com.arka.MSOrden.domain.exception.StockInsuficienteException;
import com.arka.MSOrden.domain.exception.UsuarioNotFoundException;
import com.arka.MSOrden.domain.model.Gateway.*;
import com.arka.MSOrden.domain.model.OrdenModel;
import com.arka.MSOrden.domain.model.OrdenProductoModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public class GestionOrdenesService {
    private final OrdenGateway ordenGateway;
    private final OrdenProductoGateway ordenProductoGateway;
    private final EstadoOrdenGateway estadoOrdenGateway;
    private final InventarioGateway inventarioGateway;
    private final AuthGateway authGateway;

    public GestionOrdenesService(OrdenGateway ordenGateway,
                                 OrdenProductoGateway ordenProductoGateway,
                                 EstadoOrdenGateway estadoOrdenGateway,
                                 InventarioGateway inventarioGateway,
                                 AuthGateway authGateway) {
        this.ordenGateway = ordenGateway;
        this.ordenProductoGateway = ordenProductoGateway;
        this.estadoOrdenGateway = estadoOrdenGateway;
        this.inventarioGateway = inventarioGateway;
        this.authGateway = authGateway;
    }

    /**
     * Agrega o actualiza un producto en la orden del usuario y recalcula totales.
     */
    public Mono<OrdenModel> agregarProductoAOrden(RecibirNewOrdenDto newOrdenDto) {
        List<RecibirNewOrdenProductosDto> productos = newOrdenDto.getProductos();
        if (productos == null || productos.isEmpty()) {
            return Mono.error(new EmptyProductListException());
        }

        // obtener o crear la orden
        return ordenGateway.BuscarOrdenPorIdUsuario(newOrdenDto.getIdUsuario())
                .switchIfEmpty(Mono.defer(() -> {
                    OrdenModel nuevaOrden = OrdenModel.builder()
                            .userId(newOrdenDto.getIdUsuario())
                            .creationOrden(LocalDateTime.now())
                            .fechaUltimaModificacion(LocalDateTime.now())
                            .totalPedido(0.0)
                            .totalDeProductos(0L)
                            .totalUnidades(0L)
                            .build();
                    return ordenGateway.save(nuevaOrden);
                }))
                // agregar o actualizar cada producto
                .flatMap(orden ->
                        Flux.fromIterable(productos)
                                .concatMap(pdto -> {
                                    Long cantidad = pdto.getCantidad();
                                    if (cantidad == null || cantidad <= 0L) {
                                        return Mono.error(new InvalidProductQuantityException(
                                                pdto.getProductoId(), cantidad != null ? cantidad : 0L));
                                    }
                                    // consultar inventario
                                    return inventarioGateway.consultarProducto(pdto.getProductoId())
                                            .flatMap(inv -> {
                                                // verificar stock suficiente
                                                if (inv.getStock() < cantidad) {
                                                    return Mono.error(new StockInsuficienteException(
                                                            pdto.getProductoId(), inv.getStock(), cantidad));
                                                }

                                                // calcular precio total
                                                double precioTotalCalc = cantidad.doubleValue() * inv.getPrice();

                                                // actualizar o crear detalle de producto en la orden
                                                return ordenProductoGateway.findByOrdenIdAndProductoId(orden.getId(), pdto.getProductoId())
                                                        // actualizar existente
                                                        .flatMap(ordenDetail -> {
                                                            Long previous = ordenDetail.getCantidad();
                                                            ordenDetail.setCantidad(cantidad);
                                                            ordenDetail.setPrecioTotal(precioTotalCalc);
                                                            return ordenProductoGateway.save(ordenDetail)
                                                                    // ajustar inventario según diferencia en cantidad
                                                                    .flatMap(saved -> {
                                                                        long delta = cantidad - previous;
                                                                        if (delta == 0L) {
                                                                            return Mono.just(saved);
                                                                        }
                                                                        return calcularNuevoStockInventario(pdto.getProductoId(), delta)
                                                                                .thenReturn(saved);
                                                                    });
                                                        })
                                                        // crear nuevo
                                                        .switchIfEmpty(Mono.defer(() -> {
                                                            OrdenProductoModel nuevoProd = OrdenProductoModel.builder()
                                                                    .ordenId(orden.getId())
                                                                    .productoId(pdto.getProductoId())
                                                                    .cantidad(cantidad)
                                                                    .precioTotal(precioTotalCalc)
                                                                    .build();
                                                            return ordenProductoGateway.save(nuevoProd)
                                                                    // descontar inventario
                                                                    .flatMap(saved -> calcularNuevoStockInventario(pdto.getProductoId(), cantidad)
                                                                            .thenReturn(saved));
                                                        }));
                                            });
                                })
                                .then(Mono.just(orden))
                )
                // recalcular totales de la orden
                .flatMap(this::recalcularYGuardarOrden);
    }

    public Mono<OrdenModel> recalcularYGuardarOrden(OrdenModel ordenModel) {
        // obtener todos los productos en la orden
        return ordenProductoGateway.findByOrdenId(ordenModel.getId())
                .collectList()
                .flatMap(productosEnOrden -> {
                    // recalcular totales
                    double nuevoTotalPedido = productosEnOrden.stream()
                            .mapToDouble(OrdenProductoModel::getPrecioTotal)
                            .sum();

                    long nuevoTotalUnidades = productosEnOrden.stream()
                            .mapToLong(OrdenProductoModel::getCantidad)
                            .sum();

                    long nuevoTotalDeProductos = productosEnOrden.size();

                    // actualizar y guardar la orden
                    ordenModel.setTotalPedido(nuevoTotalPedido);
                    ordenModel.setTotalUnidades(nuevoTotalUnidades);
                    ordenModel.setTotalDeProductos(nuevoTotalDeProductos);
                    ordenModel.setFechaUltimaModificacion(LocalDateTime.now());

                    return ordenGateway.save(ordenModel);
                });
    }

    public Mono<Void> calcularNuevoStockInventario(Long productoId, Long cantidadVendida) {
        return inventarioGateway.consultarProducto(productoId)
                .flatMap(inv -> {
                    long nuevoStock = inv.getStock() - cantidadVendida;
                    if (nuevoStock < 0) {
                        return Mono.error(new StockInsuficienteException(
                                productoId, inv.getStock(), cantidadVendida));
                    }
                    return inventarioGateway.actualizarStock(productoId, nuevoStock);
                });
    }

    public Mono<MostrarInformacionOrdenDto> editarOrden(Long userid, EditarProductosOrdenDto editarProductosOrdenDto) {
        if (editarProductosOrdenDto == null || editarProductosOrdenDto.getIdProducto() == null) {
            return Mono.error(new InvalidProductQuantityException("Producto inválido"));
        }
        Long idProducto = editarProductosOrdenDto.getIdProducto();
        Long nuevaCantidad = editarProductosOrdenDto.getNuevaCantidad();
        if (nuevaCantidad == null || nuevaCantidad <= 0L) {
            return Mono.error(new InvalidProductQuantityException(idProducto, nuevaCantidad != null ? nuevaCantidad : 0L));
        }

        return ordenGateway.BuscarOrdenPorIdUsuario(userid)
                .switchIfEmpty(Mono.error(new OrdenNotFoundException("No existe la orden para el usuario con ID " + userid)))
                .flatMap(orden -> {
                    if (orden.getEstadoorden() == null || orden.getEstadoorden() != 1L) {
                        return Mono.error(new IllegalStateException(
                                "Solo se pueden editar órdenes en estado PENDIENTE. Esta orden ya ha sido procesada."));
                    }
                    return Mono.just(orden);
                })
                .flatMap(orden ->
                        ordenProductoGateway.findByOrdenIdAndProductoId(orden.getId(), idProducto)
                                .flatMap(ordenProducto ->
                                        inventarioGateway.consultarProducto(idProducto)
                                                .flatMap(inv -> {
                                                    long cantidadAnterior = ordenProducto.getCantidad();
                                                    if (inv.getStock() + cantidadAnterior < nuevaCantidad) {
                                                        return Mono.error(new StockInsuficienteException(
                                                                idProducto, inv.getStock() + cantidadAnterior, nuevaCantidad));
                                                    }
                                                    double nuevoPrecioTotal = nuevaCantidad.doubleValue() * inv.getPrice();

                                                    ordenProducto.setCantidad(nuevaCantidad);
                                                    ordenProducto.setPrecioTotal(nuevoPrecioTotal);

                                                    return ordenProductoGateway.save(ordenProducto)
                                                            .flatMap(savedProd -> {
                                                                long delta = nuevaCantidad - cantidadAnterior;
                                                                if (delta == 0L) {
                                                                    return Mono.just(savedProd);
                                                                }
                                                                return calcularNuevoStockInventario(idProducto, delta)
                                                                        .thenReturn(savedProd);
                                                            });
                                                })
                                )
                                .switchIfEmpty(Mono.defer(() ->
                                        inventarioGateway.consultarProducto(idProducto)
                                                .flatMap(inv -> {
                                                    if (inv.getStock() < nuevaCantidad) {
                                                        return Mono.error(new StockInsuficienteException(
                                                                idProducto, inv.getStock(), nuevaCantidad));
                                                    }
                                                    OrdenProductoModel nuevoProd = OrdenProductoModel.builder()
                                                            .ordenId(orden.getId())
                                                            .productoId(idProducto)
                                                            .cantidad(nuevaCantidad)
                                                            .precioTotal(nuevaCantidad.doubleValue() * inv.getPrice())
                                                            .build();
                                                    return ordenProductoGateway.save(nuevoProd)
                                                            .flatMap(saved -> calcularNuevoStockInventario(idProducto, nuevaCantidad)
                                                                    .thenReturn(saved));
                                                })
                                ))
                                .flatMap(savedOrdenProd -> recalcularYGuardarOrden(orden))
                )
                .flatMap(updatedOrden -> obtenerInformacionOrdenPorUsuario(userid));
    }

    // elimina un producto de la orden del usuario
    public Mono<MostrarInformacionOrdenDto> eliminarProductoDeOrden(Long userId, Long productoId) {
        // Validar que el productoId no sea nulo
        if (productoId == null || productoId <= 0) {
            return Mono.error(new InvalidProductQuantityException("ID de producto inválido"));
        }

        return ordenGateway.BuscarOrdenPorIdUsuario(userId)
                .switchIfEmpty(Mono.error(new OrdenNotFoundException("No existe la orden para el usuario con ID " + userId)))
                .flatMap(orden -> {
                    // Validar que la orden esté en estado PENDIENTE (1)
                    if (orden.getEstadoorden() == null || orden.getEstadoorden() != 1L) {
                        return Mono.error(new IllegalStateException(
                                "Solo se pueden eliminar productos de órdenes en estado PENDIENTE. Esta orden ya ha sido procesada."));
                    }
                    return Mono.just(orden);
                })
                .flatMap(orden ->
                        // Buscar el producto en la orden
                        ordenProductoGateway.findByOrdenIdAndProductoId(orden.getId(), productoId)
                                .switchIfEmpty(Mono.error(new InvalidProductQuantityException(
                                        "El producto con ID " + productoId + " no existe en la orden")))
                                .flatMap(ordenProducto -> {
                                    // Guardar la cantidad antes de eliminar para devolverla al inventario
                                    Long cantidadADevolver = ordenProducto.getCantidad();

                                    // Eliminar el producto de la orden
                                    return ordenProductoGateway.deleteByOrdenIdAndProductoId(orden.getId(), productoId)
                                            .then(Mono.defer(() -> {
                                                // Devolver el stock al inventario
                                                return inventarioGateway.consultarProducto(productoId)
                                                        .flatMap(inv -> {
                                                            long nuevoStock = inv.getStock() + cantidadADevolver;
                                                            return inventarioGateway.actualizarStock(productoId, nuevoStock);
                                                        })
                                                        .onErrorResume(ex -> {
                                                            // Si falla la actualización del inventario, lo registramos pero no fallamos la operación
                                                            // ya que el producto ya fue eliminado de la orden
                                                            return Mono.empty();
                                                        });
                                            }))
                                            .then(Mono.just(orden));
                                })
                )
                .flatMap(orden ->
                        // Recalcular los totales de la orden después de eliminar el producto
                        recalcularYGuardarOrden(orden)
                )
                .flatMap(ordenActualizada ->
                        // Retornar la información actualizada de la orden
                        obtenerInformacionOrdenPorUsuario(userId)
                );
    }

    public Mono<MostrarInformacionOrdenDto> obtenerInformacionOrdenPorUsuario(Long userId) {
        return ordenGateway.BuscarOrdenPorIdUsuario(userId)
                .switchIfEmpty(Mono.error(new OrdenNotFoundException("No existe la orden para el usuario con ID " + userId)))
                .flatMap(orden ->
                        authGateway.consultarUsuario(orden.getUserId())
                                .switchIfEmpty(Mono.error(new UsuarioNotFoundException(orden.getUserId())))
                                .flatMap(userAuth ->
                                        ordenProductoGateway.findByOrdenId(orden.getId())
                                                .flatMap(prod ->
                                                        inventarioGateway.consultarProducto(prod.getProductoId())
                                                                .map(prodInv -> MostrarInformacionProductosDto.builder()
                                                                        .idProducto(prod.getProductoId())
                                                                        .nombreProducto(prodInv.getNombre())
                                                                        .descripcionProducto(prodInv.getDescripcion())
                                                                        .precioUnitario(prodInv.getPrice())
                                                                        .precioTotal(prod.getPrecioTotal())
                                                                        .cantidad(prod.getCantidad().intValue())
                                                                        .build())
                                                                .onErrorResume(ex -> Mono.just(MostrarInformacionProductosDto.builder()
                                                                        .idProducto(prod.getProductoId())
                                                                        .nombreProducto("Desconocido")
                                                                        .descripcionProducto("Desconocido")
                                                                        .precioUnitario(0.0)
                                                                        .precioTotal(prod.getPrecioTotal())
                                                                        .cantidad(prod.getCantidad().intValue())
                                                                        .build()))
                                                )
                                                .collectList()
                                                .flatMap(productos ->
                                                        estadoOrdenGateway.buscarEstadoPorId(orden.getEstadoorden())
                                                                .map(estado -> MostrarInformacionOrdenDto.builder()
                                                                        .idOrden(orden.getId())
                                                                        .idUsuario(orden.getUserId())
                                                                        .nombreUsuario(userAuth.getName())
                                                                        .direccionEnvio(userAuth.getDireccion())
                                                                        .emailUsuario(userAuth.getEmail())
                                                                        .numeroContacto(userAuth.getTelefono())
                                                                        .estadoOrden(estado.getNombreEstado())
                                                                        .totalOrden(orden.getTotalPedido())
                                                                        .numeroProductos(orden.getTotalDeProductos())
                                                                        .cantidadTotalProductos(orden.getTotalUnidades())
                                                                        .fechaCreacion(orden.getCreationOrden())
                                                                        .productos(productos)
                                                                        .build()
                                                                )
                                                )
                                )
                );
    }
}
