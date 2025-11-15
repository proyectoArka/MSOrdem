package com.arka.MSOrden.infrastructure.DrivenAdapter.model;

import com.arka.MSOrden.domain.model.OrdenProductoModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Entidad R2DBC para el detalle de productos de una orden.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orden_producto")
public class OrdenProductoEntity {
    @Id
    private Long id;
    private Long ordenId;
    private Long productoId;
    private Long cantidad;
    private Double precioTotal;

    public static OrdenProductoEntity fromDomain(OrdenProductoModel model) {
        if (model == null) return null;
        return OrdenProductoEntity.builder()
                .id(model.getId())
                .ordenId(model.getOrdenId())
                .productoId(model.getProductoId())
                .cantidad(model.getCantidad())
                .precioTotal(model.getPrecioTotal())
                .build();
    }

    public OrdenProductoModel toDomain() {
        return OrdenProductoModel.builder()
                .id(this.id)
                .ordenId(this.ordenId)
                .productoId(this.productoId)
                .cantidad(this.cantidad)
                .precioTotal(this.precioTotal)
                .build();
    }
}