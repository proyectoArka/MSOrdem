package com.arka.MSOrden.infrastructure.DrivenAdapter.repository;

import com.arka.MSOrden.infrastructure.DrivenAdapter.model.EstadoOrdenEntity;
import com.arka.MSOrden.infrastructure.DrivenAdapter.model.OrdenEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EstadoOrdenEntityRepository extends R2dbcRepository<EstadoOrdenEntity, Long> {
    Mono<EstadoOrdenEntity> findById(Long id);
}
