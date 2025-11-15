package com.arka.MSOrden.application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditarProductosOrdenDto {

    @JsonProperty("idProducto")
    private Long idProducto;

    @JsonProperty("nuevaCantidad")
    private Long nuevaCantidad;

}
