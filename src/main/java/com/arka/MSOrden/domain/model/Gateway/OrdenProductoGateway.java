package com.arka.MSOrden.domain.model.Gateway;

import com.arka.MSOrden.domain.model.OrdenProductoModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrdenProductoGateway {
    Flux<OrdenProductoModel> findByOrdenId(Long ordenId);
    Mono<OrdenProductoModel> findByOrdenIdAndProductoId(Long ordenId, Long productoId);
    Mono<OrdenProductoModel> save(OrdenProductoModel ordenProductoModel);
}
