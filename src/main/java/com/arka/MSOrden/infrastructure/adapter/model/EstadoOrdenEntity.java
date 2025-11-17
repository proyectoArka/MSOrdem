package com.arka.MSOrden.infrastructure.adapter.model;

import com.arka.MSOrden.domain.model.EstadoOrden;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Table("estado")
public class EstadoOrdenEntity {
    @Id
    @Column("id")
    private Long id;
    @Column("nombre_estado")
    private String nombreEstado;


    public EstadoOrden toDomain() {
        return EstadoOrden.builder()
                .id(this.id)
                .nombreEstado(this.nombreEstado)
                .build();
    }

}

