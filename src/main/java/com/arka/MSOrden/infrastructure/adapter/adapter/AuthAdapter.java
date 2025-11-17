package com.arka.MSOrden.infrastructure.adapter.adapter;

import com.arka.MSOrden.application.dto.ConsultUserInAuthDto;
import com.arka.MSOrden.domain.exception.UsuarioNotFoundException;
import com.arka.MSOrden.domain.model.Gateway.AuthGateway;
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
 * Adaptador para comunicación con el microservicio de Autenticación.
 * Implementa el Gateway usando WebClient con LoadBalancer.
 */
@Slf4j
@Component
public class AuthAdapter implements AuthGateway {

    private final WebClient webClient;
    private final ExternalServicesProperties.AuthConfig config;

    public AuthAdapter(WebClient.Builder loadBalancedWebClientBuilder,
                      ExternalServicesProperties externalServicesProperties) {
        this.config = externalServicesProperties.getAuth();
        this.webClient = loadBalancedWebClientBuilder.build();
    }

    @Override
    public Mono<ConsultUserInAuthDto> consultarUsuario(Long userId) {
        String url = config.getBaseUrl() + config.getConsultar();

        log.debug("Consultando usuario {} en servicio de autenticación: {}", userId, url);

        return webClient.get()
                .uri(url, userId)
                .retrieve()
                .bodyToMono(ConsultUserInAuthDto.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.error("Usuario {} no encontrado en servicio de autenticación", userId);
                    return Mono.error(new UsuarioNotFoundException(userId));
                })
                .onErrorResume(WebClientResponseException.InternalServerError.class, ex -> {
                    log.error("Error interno en servicio de auth al consultar usuario {}", userId);
                    return Mono.error(new UsuarioNotFoundException(
                            "Error interno al consultar usuario con ID " + userId));
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("No se pudo conectar al servicio de autenticación: {}", ex.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                            "Servicio de autenticación no disponible. Por favor intente más tarde."));
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof ResponseStatusException ||
                        throwable instanceof UsuarioNotFoundException) {
                        return Mono.error(throwable);
                    }
                    log.error("Error inesperado al consultar usuario {}: {}", userId, throwable.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error al consultar información del usuario", throwable));
                });
    }
}

