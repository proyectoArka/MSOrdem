package com.arka.MSOrden.domain.model.Gateway;

import com.arka.MSOrden.application.dto.ConsultUserInAuthDto;
import reactor.core.publisher.Mono;

/**
 * Gateway para comunicación con el microservicio de Autenticación.
 * Define el contrato de negocio sin depender de implementación técnica.
 */
public interface AuthGateway {

    /**
     * Consulta información de un usuario en el servicio de autenticación
     * @param userId ID del usuario
     * @return Información del usuario
     */
    Mono<ConsultUserInAuthDto> consultarUsuario(Long userId);
}

