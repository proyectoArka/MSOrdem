package com.arka.MSOrden.application.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultUserInAuthDto {
    private String name;
    private String email;
    private String direccion;
    private String telefono;
}
