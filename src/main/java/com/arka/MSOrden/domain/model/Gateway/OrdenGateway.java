package com.arka.MSOrden.domain.model.Gateway;

import com.arka.MSOrden.domain.model.OrdenModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrdenGateway {
    Mono<OrdenModel> BuscarOrdenPorIdUsuario(Long userId);
    Mono<OrdenModel> findById(Long ordenId);
    Mono<OrdenModel> save(OrdenModel ordenModel);
    Flux<OrdenModel> findAll();
}
