package com.arka.MSOrden.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external.services")
public class ExternalServicesProperties {

    private InventarioConfig inventario = new InventarioConfig();
    private AuthConfig auth = new AuthConfig();

    public static class InventarioConfig {
        private String baseUrl;
        private String consultar;
        private String actualizar;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getConsultar() {
            return consultar;
        }

        public void setConsultar(String consultar) {
            this.consultar = consultar;
        }

        public String getActualizar() {
            return actualizar;
        }

        public void setActualizar(String actualizar) {
            this.actualizar = actualizar;
        }
    }

    public static class AuthConfig {
        private String baseUrl;
        private String consultar;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getConsultar() {
            return consultar;
        }

        public void setConsultar(String consultar) {
            this.consultar = consultar;
        }
    }

    public InventarioConfig getInventario() {
        return inventario;
    }

    public void setInventario(InventarioConfig inventario) {
        this.inventario = inventario;
    }

    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }
}

