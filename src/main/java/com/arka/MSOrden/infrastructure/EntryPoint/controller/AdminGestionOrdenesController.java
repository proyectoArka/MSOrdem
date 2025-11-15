package com.arka.MSOrden.infrastructure.EntryPoint.controller;

import com.arka.MSOrden.application.dtos.DetalleOrdenDto;
import com.arka.MSOrden.application.dtos.ListarOrdenDto;
import com.arka.MSOrden.application.dtos.MostrarInformacionOrdenDto;
import com.arka.MSOrden.domain.usecase.AdminOrdenesService;
import com.arka.MSOrden.domain.usecase.EstadoOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/ordenes/admin")
@RequiredArgsConstructor
public class AdminGestionOrdenesController {

    private final EstadoOrdenService estadoOrdenService;
    private final AdminOrdenesService adminOrdenesService;

    @GetMapping("/todaslasordenes")
    public Flux<ListarOrdenDto> obtenerTodasLasOrdenes() {
        return adminOrdenesService.obtenerTodasLasOrdenes();
    }

    @GetMapping("/detalleorden/{ordenId}")
    public Mono<ResponseEntity<DetalleOrdenDto>> obtenerDetalleOrden(@PathVariable Long ordenId) {
        return adminOrdenesService.obtenerDetalleOrden(ordenId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/despacharorden/{ordenId}")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> despacharOrden(@PathVariable Long ordenId) {
        return estadoOrdenService.despacharOrden(ordenId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/entregarorden/{ordenId}")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> entregarOrden(@PathVariable Long ordenId) {
        return estadoOrdenService.entregarOrden(ordenId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
