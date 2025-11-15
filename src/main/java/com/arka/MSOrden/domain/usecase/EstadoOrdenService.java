package com.arka.MSOrden.domain.usecase;

import com.arka.MSOrden.application.dtos.MostrarInformacionOrdenDto;
import com.arka.MSOrden.domain.model.Gateway.OrdenGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class EstadoOrdenService {

    private static final Logger log = LoggerFactory.getLogger(EstadoOrdenService.class);

    private final GestionOrdenesService gestionOrdenesService;
    private final OrdenGateway ordenGateway;
    private final NotificacionEmailService notificacionEmailService;

    private static final Long ESTADO_CONFIRMADO = 2L;
    private static final Long ESTADO_DESPACHADO = 3L;
    private static final Long ESTADO_ENTREGADO = 4L;

    public EstadoOrdenService(OrdenGateway ordenRepository,
                              GestionOrdenesService gestionOrdenesService,
                              NotificacionEmailService notificacionEmailService) {
        this.ordenGateway = ordenRepository;
        this.gestionOrdenesService = gestionOrdenesService;
        this.notificacionEmailService = notificacionEmailService;
    }

    public Mono<MostrarInformacionOrdenDto> confirmarOrden(Long userId) {
        return cambiarEstadoOrdenPorUserId(userId, ESTADO_CONFIRMADO);
    }

    public Mono<MostrarInformacionOrdenDto> despacharOrden(Long ordenId) {
        return cambiarEstadoOrdenPorOrdenId(ordenId, ESTADO_DESPACHADO);
    }

    public Mono<MostrarInformacionOrdenDto> entregarOrden(Long ordenId) {
        return cambiarEstadoOrdenPorOrdenId(ordenId, ESTADO_ENTREGADO);
    }

    private Mono<MostrarInformacionOrdenDto> cambiarEstadoOrdenPorUserId(Long userId, Long nuevoEstado) {
        return ordenGateway.BuscarOrdenPorIdUsuario(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada")))
                .flatMap(orden -> {
                    orden.setEstadoorden(nuevoEstado);
                    orden.setFechaUltimaModificacion(LocalDateTime.now());
                    return ordenGateway.save(orden);
                })
                .flatMap(updatedOrden -> gestionOrdenesService.obtenerInformacionOrdenPorUsuario(userId))
                .flatMap(ordenInfo -> {
                    // Enviar notificación por email de forma asíncrona (no bloqueante)
                    notificacionEmailService.enviarNotificacionCambioEstado(ordenInfo)
                            .doOnError(error -> log.error("Error al enviar notificación de email", error))
                            .subscribe();

                    log.info("Orden #{} cambió a estado: {} - Notificación enviada a: {}",
                            ordenInfo.getIdOrden(), ordenInfo.getEstadoOrden(), ordenInfo.getEmailUsuario());

                    return Mono.just(ordenInfo);
                });
    }

    private Mono<MostrarInformacionOrdenDto> cambiarEstadoOrdenPorOrdenId(Long ordenId, Long nuevoEstado) {
        return ordenGateway.findById(ordenId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada")))
                .flatMap(orden -> {
                    orden.setEstadoorden(nuevoEstado);
                    orden.setFechaUltimaModificacion(LocalDateTime.now());
                    return ordenGateway.save(orden);
                })
                .flatMap(updatedOrden -> gestionOrdenesService.obtenerInformacionOrdenPorUsuario(updatedOrden.getUserId()))
                .flatMap(ordenInfo -> {
                    // Enviar notificación por email de forma asíncrona (no bloqueante)
                    notificacionEmailService.enviarNotificacionCambioEstado(ordenInfo)
                            .doOnError(error -> log.error("Error al enviar notificación de email", error))
                            .subscribe();

                    log.info("Orden #{} cambió a estado: {} - Notificación enviada a: {}",
                            ordenInfo.getIdOrden(), ordenInfo.getEstadoOrden(), ordenInfo.getEmailUsuario());

                    return Mono.just(ordenInfo);
                });
    }

    public void notificationEstadoOrdenService(){

    }
}