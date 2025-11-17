package com.arka.MSOrden.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para editar la cantidad de un producto en la orden")
public class EditarProductosOrdenDto {

    @JsonProperty("idProducto")
    @Schema(description = "ID del producto a editar", example = "1", required = true)
    private Long idProducto;

    @JsonProperty("nuevaCantidad")
    @Schema(description = "Nueva cantidad del producto", example = "10", required = true, minimum = "1")
    private Long nuevaCantidad;

}
