package com.arka.MSOrden.domain.model.Gateway;

import com.arka.MSOrden.domain.model.EstadoOrden;
import reactor.core.publisher.Mono;

public interface EstadoOrdenGateway {
    Mono<EstadoOrden> buscarEstadoPorId(Long id);
}
