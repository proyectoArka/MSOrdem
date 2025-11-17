package com.arka.MSOrden.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI 3.0 para documentación Swagger.
 * Proporciona información detallada sobre la API de gestión de órdenes.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:MSOrden}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Órdenes - Microservicio")
                        .description("""
                                API REST reactiva para la gestión completa de órdenes de compra.
                                
                                **Características principales:**
                                - Creación y gestión de órdenes de compra
                                - Gestión de productos en órdenes (agregar, editar, eliminar)
                                - Integración con microservicio de Inventario
                                - Integración con microservicio de Autenticación
                                - Confirmación de órdenes
                                - Consulta de información detallada de órdenes
                                
                                **Tecnologías:**
                                - Spring Boot 3.5.7
                                - Spring WebFlux (Programación Reactiva)
                                - R2DBC (Base de datos reactiva)
                                - PostgreSQL
                                - Spring Cloud (Eureka, Config Server, Load Balancer)
                                
                                **Arquitectura:**
                                - Clean Architecture (Arquitectura Limpia)
                    
                                """)
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("http://localhost:8888")
                                .description("API Gateway")
                ));
    }
}

