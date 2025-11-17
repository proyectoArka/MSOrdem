package com.arka.MSOrden.infrastructure.adapter.repository;

import com.arka.MSOrden.infrastructure.adapter.model.OrdenProductoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrdenProductoRepository extends R2dbcRepository<OrdenProductoEntity, Long> {
    Mono<OrdenProductoEntity> findByOrdenIdAndProductoId(Long ordenId, Long productoId);
    Flux<OrdenProductoEntity> findByOrdenId(Long ordenId);
    Mono<Void> deleteByOrdenIdAndProductoId(Long ordenId, Long productoId);
}
