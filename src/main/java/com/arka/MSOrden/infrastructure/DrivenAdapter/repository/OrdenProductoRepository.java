package com.arka.MSOrden.infrastructure.DrivenAdapter.repository;

import com.arka.MSOrden.infrastructure.DrivenAdapter.model.OrdenProductoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrdenProductoRepository extends R2dbcRepository<OrdenProductoEntity, Long> {
    Mono<OrdenProductoEntity> findByOrdenIdAndProductoId(Long ordenId, Long productoId);
    Flux<OrdenProductoEntity> findByOrdenId(Long ordenId);
}
