package com.arka.MSOrden.infrastructure.adapter.repository;

import com.arka.MSOrden.infrastructure.adapter.model.EstadoOrdenEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EstadoOrdenEntityRepository extends R2dbcRepository<EstadoOrdenEntity, Long> {
    Mono<EstadoOrdenEntity> findById(Long id);
}
