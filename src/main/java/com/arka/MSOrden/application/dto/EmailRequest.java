package com.arka.MSOrden.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
    private String destination;
    private String asunto;
    private String cuerpoMensaje;
    private String tipoEvento;
}

