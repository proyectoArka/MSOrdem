package com.arka.MSOrden.infrastructure.DrivenAdapter.adapter;

import com.arka.MSOrden.domain.model.EstadoOrden;
import com.arka.MSOrden.domain.model.Gateway.EstadoOrdenGateway;
import com.arka.MSOrden.infrastructure.DrivenAdapter.model.EstadoOrdenEntity;
import com.arka.MSOrden.infrastructure.DrivenAdapter.repository.EstadoOrdenEntityRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EstadoOrdenAdapter implements EstadoOrdenGateway {

    private final EstadoOrdenEntityRepository estadoOrdenEntityRepository;

    public EstadoOrdenAdapter(EstadoOrdenEntityRepository estadoOrdenEntityRepository) {
        this.estadoOrdenEntityRepository = estadoOrdenEntityRepository;
    }

    @Override
    public Mono<EstadoOrden> buscarEstadoPorId(Long id) {
        return estadoOrdenEntityRepository.findById(id)
                .map(EstadoOrdenEntity::toDomain);

    }
}
