package com.arka.MSOrden.infrastructure.EntryPoint.controller;

import com.arka.MSOrden.application.dtos.EditarProductosOrdenDto;
import com.arka.MSOrden.application.dtos.MostrarInformacionOrdenDto;
import com.arka.MSOrden.application.dtos.RecibirNewOrdenDto;
import com.arka.MSOrden.domain.model.OrdenModel;
import com.arka.MSOrden.domain.usecase.EstadoOrdenService;
import com.arka.MSOrden.domain.usecase.GestionOrdenesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class GestionOrdenesController {

    private final GestionOrdenesService gestionOrdenesService;
    private final EstadoOrdenService estadoOrdenService;

    @PostMapping("/neworden")
    public Mono<ResponseEntity<OrdenModel>> newOrden(@RequestBody RecibirNewOrdenDto newOrdenDto) {
        System.out.println("id cliente " + newOrdenDto.getIdUsuario());
        return gestionOrdenesService.agregarProductoAOrden(newOrdenDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/editarorden")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> editarorden(
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId,
            @RequestBody EditarProductosOrdenDto editarProductosOrdenDto) {


        // logs claros (dto.toString() generado por Lombok)
        System.out.println("Usuario ID efectivo: " + userId);
        System.out.println("EditarProductosOrdenDto recibido: " + editarProductosOrdenDto);

        return gestionOrdenesService.editarOrden(userId, editarProductosOrdenDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/confirmarorden")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> confirmarOrden(@RequestHeader("X-Auth-User-Id")Long userId) {
        return estadoOrdenService.confirmarOrden(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
