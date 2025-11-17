package com.arka.MSOrden.infrastructure.adapter.adapter;

import com.arka.MSOrden.application.dto.ConsultProductInventarioDto;
import com.arka.MSOrden.application.dto.StockUpdateDto;
import com.arka.MSOrden.domain.exception.ProductoNotFoundException;
import com.arka.MSOrden.domain.model.Gateway.InventarioGateway;
import com.arka.MSOrden.infrastructure.config.ExternalServicesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Adaptador para comunicación con el microservicio de Inventario.
 * Implementa el Gateway usando WebClient con LoadBalancer.
 */
@Slf4j
@Component
public class InventarioAdapter implements InventarioGateway {

    private final WebClient webClient;
    private final ExternalServicesProperties.InventarioConfig config;

    public InventarioAdapter(WebClient.Builder loadBalancedWebClientBuilder,
                            ExternalServicesProperties externalServicesProperties) {
        this.config = externalServicesProperties.getInventario();
        this.webClient = loadBalancedWebClientBuilder.build();
    }

    @Override
    public Mono<ConsultProductInventarioDto> consultarProducto(Long productoId) {
        String url = config.getBaseUrl() + config.getConsultar();

        log.debug("Consultando producto {} en inventario: {}", productoId, url);

        return webClient.get()
                .uri(url, productoId)
                .retrieve()
                .bodyToMono(ConsultProductInventarioDto.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.error("Producto {} no encontrado en inventario", productoId);
                    return Mono.error(new ProductoNotFoundException(productoId));
                })
                .onErrorResume(WebClientResponseException.InternalServerError.class, ex -> {
                    log.error("Error interno en servicio de inventario al consultar producto {}", productoId);
                    return Mono.error(new ProductoNotFoundException(
                            "Error interno al consultar producto con ID " + productoId));
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("No se pudo conectar al servicio de inventario: {}", ex.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                            "Servicio de inventario no disponible. Por favor intente más tarde."));
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof ResponseStatusException ||
                        throwable instanceof ProductoNotFoundException) {
                        return Mono.error(throwable);
                    }
                    log.error("Error inesperado al consultar producto {}: {}", productoId, throwable.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error al consultar el inventario", throwable));
                });
    }

    @Override
    public Mono<Void> actualizarStock(Long productoId, Long nuevoStock) {
        String url = config.getBaseUrl() + config.getActualizar();
        StockUpdateDto body = new StockUpdateDto(nuevoStock);

        log.debug("Actualizando stock del producto {} a {}", productoId, nuevoStock);

        return webClient.put()
                .uri(url, productoId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.error("Producto {} no encontrado al actualizar stock", productoId);
                    return Mono.error(new ProductoNotFoundException(
                            "No se pudo actualizar el stock. Producto con ID " + productoId + " no encontrado"));
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("No se pudo conectar al servicio de inventario para actualizar stock: {}", ex.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                            "No se pudo conectar al servicio de inventario para actualizar el stock"));
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof ResponseStatusException ||
                        throwable instanceof ProductoNotFoundException) {
                        return Mono.error(throwable);
                    }
                    log.error("Error inesperado al actualizar stock del producto {}: {}", productoId, throwable.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error al actualizar el stock", throwable));
                });
    }
}

