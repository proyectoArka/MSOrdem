package com.arka.MSOrden.infrastructure.DrivenAdapter.adapter;

import com.arka.MSOrden.domain.model.Gateway.OrdenGateway;
import com.arka.MSOrden.domain.model.OrdenModel;
import com.arka.MSOrden.infrastructure.DrivenAdapter.model.OrdenEntity;
import com.arka.MSOrden.infrastructure.DrivenAdapter.repository.OrdenEntityRepository;
import com.arka.MSOrden.infrastructure.DrivenAdapter.repository.OrdenProductoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class OrdenModelAdapter implements OrdenGateway {

    private final OrdenEntityRepository repository;
    private final OrdenProductoRepository ordenProductoRepository;
    private static final Long ESTADO_POR_DEFECTO = 1l;

    public OrdenModelAdapter(OrdenEntityRepository repository,
                             OrdenProductoRepository ordenProductoRepository) {
        this.repository = repository;
        this.ordenProductoRepository = ordenProductoRepository;
    }

    @Override
    public Mono<OrdenModel> BuscarOrdenPorIdUsuario(Long userId) {
        return repository.findByUserId(userId)
                .map(OrdenEntity::toDomain);
    }

    @Override
    public Mono<OrdenModel> findById(Long ordenId) {
        return repository.findById(ordenId)
                .map(OrdenEntity::toDomain);
    }

    @Override
    public Mono<OrdenModel> save(OrdenModel ordenModel) {
        OrdenEntity entity = OrdenEntity.fromDomain(ordenModel);
        if (entity.getEstadoOrden() == null) {
            entity.setEstadoOrden(ESTADO_POR_DEFECTO);
        }
        return repository.save(entity)
                .map(OrdenEntity::toDomain);
    }

    @Override
    public Flux<OrdenModel> findAll() {
        return repository.findAll()
                .map(OrdenEntity::toDomain);
    }
}
