package com.arka.MSOrden.infrastructure.config;

import com.arka.MSOrden.domain.model.Gateway.*;
import com.arka.MSOrden.domain.usecase.AdminOrdenesService;
import com.arka.MSOrden.domain.usecase.EstadoOrdenService;
import com.arka.MSOrden.domain.usecase.GestionOrdenesService;
import com.arka.MSOrden.domain.usecase.NotificacionEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public NotificacionEmailService notificacionEmailService() {
        return new NotificacionEmailService();
    }

    @Bean
    public GestionOrdenesService gestionOrdenesService(OrdenGateway ordenGateway,
                                                       OrdenProductoGateway ordenProductoGateway,
                                                       EstadoOrdenGateway estadoOrdenGateway,
                                                       InventarioGateway inventarioGateway,
                                                       AuthGateway authGateway) {
        return new GestionOrdenesService(ordenGateway,
                ordenProductoGateway,
                estadoOrdenGateway,
                inventarioGateway,
                authGateway);
    }

    @Bean
    public EstadoOrdenService estadoOrdenService(OrdenGateway ordenGateway,
                                                 GestionOrdenesService gestionOrdenesService,
                                                 NotificacionEmailService notificacionEmailService) {
        return new EstadoOrdenService(ordenGateway, gestionOrdenesService, notificacionEmailService);
    }

    @Bean
    public AdminOrdenesService adminOrdenesService(OrdenGateway ordenGateway,
                                                   EstadoOrdenGateway estadoOrdenGateway,
                                                   OrdenProductoGateway ordenProductoGateway,
                                                   AuthGateway authGateway,
                                                   InventarioGateway inventarioGateway) {
        return new AdminOrdenesService(ordenGateway, estadoOrdenGateway,
                ordenProductoGateway, authGateway, inventarioGateway);
    }
}

