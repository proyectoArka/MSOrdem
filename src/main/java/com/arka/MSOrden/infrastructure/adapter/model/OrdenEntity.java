package com.arka.MSOrden.infrastructure.adapter.model;

import com.arka.MSOrden.domain.model.OrdenModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Entidad R2DBC para la tabla de ordenes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orden")
public class OrdenEntity {
    @Id
    private Long id;
    @Column("user_id")
    private Long userId;
    private LocalDateTime creationOrden;
    private LocalDateTime fechaUltimaModificacion;
    private Double totalPedido;
    private Long totalDeProductos;
    private Long totalUnidades;
    @Column("estado_orden")
    private Long estadoOrden;

    public OrdenModel toDomain() {
        return OrdenModel.builder()
                .id(this.id)
                .userId(this.userId)
                .creationOrden(this.creationOrden)
                .fechaUltimaModificacion(this.fechaUltimaModificacion)
                .totalPedido(this.totalPedido == null ? 0.0 : this.totalPedido)
                .totalDeProductos(this.totalDeProductos == null ? 0L : this.totalDeProductos)
                .totalUnidades(this.totalUnidades == null ? 0L : this.totalUnidades)
                .estadoorden(this.estadoOrden)
                .build();
    }

    public static OrdenEntity fromDomain(OrdenModel model) {
        if (model == null) return null;
        return OrdenEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .creationOrden(model.getCreationOrden())
                .fechaUltimaModificacion(model.getFechaUltimaModificacion())
                .totalPedido(model.getTotalPedido())
                .totalDeProductos(model.getTotalDeProductos())
                .totalUnidades(model.getTotalUnidades())
                .estadoOrden(model.getEstadoorden())
                .build();
    }
}
