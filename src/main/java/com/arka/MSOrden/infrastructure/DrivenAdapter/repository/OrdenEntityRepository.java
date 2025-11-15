package com.arka.MSOrden.infrastructure.DrivenAdapter.repository;

import com.arka.MSOrden.infrastructure.DrivenAdapter.model.OrdenEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface OrdenEntityRepository extends R2dbcRepository<OrdenEntity, Long> {
    Mono<OrdenEntity> findByUserId(Long userId);
}
