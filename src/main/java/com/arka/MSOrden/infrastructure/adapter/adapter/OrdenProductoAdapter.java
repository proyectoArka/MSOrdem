package com.arka.MSOrden.infrastructure.adapter.adapter;

import com.arka.MSOrden.domain.model.Gateway.OrdenProductoGateway;
import com.arka.MSOrden.domain.model.OrdenProductoModel;
import com.arka.MSOrden.infrastructure.adapter.model.OrdenProductoEntity;
import com.arka.MSOrden.infrastructure.adapter.repository.OrdenProductoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OrdenProductoAdapter implements OrdenProductoGateway {

    private final OrdenProductoRepository repository;

    public OrdenProductoAdapter(OrdenProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<OrdenProductoModel> findByOrdenIdAndProductoId(Long ordenId, Long productoId) {
        return repository.findByOrdenIdAndProductoId(ordenId, productoId)
                .map(OrdenProductoEntity::toDomain);
    }

    @Override
    public Mono<OrdenProductoModel> save(OrdenProductoModel ordenProductoModel) {
        OrdenProductoEntity entity = OrdenProductoEntity.fromDomain(ordenProductoModel);
        return repository.save(entity)
                .map(OrdenProductoEntity::toDomain);
    }

    @Override
    public Flux<OrdenProductoModel> findByOrdenId(Long ordenId) {
        return repository.findByOrdenId(ordenId)
                .map(OrdenProductoEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteByOrdenIdAndProductoId(Long ordenId, Long productoId) {
        return repository.deleteByOrdenIdAndProductoId(ordenId, productoId);
    }
}
