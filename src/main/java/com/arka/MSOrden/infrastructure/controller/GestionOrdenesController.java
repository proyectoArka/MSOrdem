package com.arka.MSOrden.infrastructure.controller;

import com.arka.MSOrden.application.dto.EditarProductosOrdenDto;
import com.arka.MSOrden.application.dto.ErrorResponseDto;
import com.arka.MSOrden.application.dto.MostrarInformacionOrdenDto;
import com.arka.MSOrden.application.dto.RecibirNewOrdenDto;
import com.arka.MSOrden.domain.model.OrdenModel;
import com.arka.MSOrden.domain.usecase.EstadoOrdenService;
import com.arka.MSOrden.domain.usecase.GestionOrdenesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@Tag(name = "Gestión de Órdenes", description = "Endpoints para la gestión completa de órdenes de compra")
public class GestionOrdenesController {

    private final GestionOrdenesService gestionOrdenesService;
    private final EstadoOrdenService estadoOrdenService;

    @Operation(
            summary = "Crear o actualizar orden con productos",
            description = """
                    Crea una nueva orden para el usuario o actualiza una orden existente agregando productos.
                    
                    **Funcionalidad:**
                    - Si el usuario no tiene una orden, se crea una nueva
                    - Si ya existe una orden en estado PENDIENTE, se agregan/actualizan los productos
                    - Valida stock disponible en inventario antes de agregar
                    - Actualiza el inventario descontando las cantidades
                    - Recalcula automáticamente los totales de la orden
                    
                    **Validaciones:**
                    - Lista de productos no puede estar vacía
                    - Cantidades deben ser mayores a 0
                    - Debe haber stock suficiente en inventario
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden creada o actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdenModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o stock insuficiente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado en inventario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/neworden")
    public Mono<ResponseEntity<OrdenModel>> newOrden(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información de la orden y productos a agregar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecibirNewOrdenDto.class))
            )
            @RequestBody RecibirNewOrdenDto newOrdenDto) {
        System.out.println("id cliente " + newOrdenDto.getIdUsuario());
        return gestionOrdenesService.agregarProductoAOrden(newOrdenDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Editar cantidad de un producto en la orden",
            description = """
                    Modifica la cantidad de un producto específico en la orden del usuario.
                    
                    **Funcionalidad:**
                    - Actualiza la cantidad de un producto existente en la orden
                    - Si el producto no existe en la orden, lo agrega
                    - Valida y ajusta el stock en inventario según la diferencia
                    - Recalcula automáticamente los totales de la orden
                    - Solo permite editar órdenes en estado PENDIENTE
                    
                    **Validaciones:**
                    - Solo se pueden editar órdenes en estado PENDIENTE
                    - La cantidad debe ser mayor a 0
                    - Debe haber stock suficiente considerando la cantidad anterior
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MostrarInformacionOrdenDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cantidad inválida, stock insuficiente o estado no permitido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden o producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping(value = "/editarorden")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> editarorden(
            @Parameter(description = "ID del usuario autenticado", required = true)
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ID del producto y nueva cantidad",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EditarProductosOrdenDto.class))
            )
            @RequestBody EditarProductosOrdenDto editarProductosOrdenDto) {


        // logs claros (dto.toString() generado por Lombok)
        System.out.println("Usuario ID efectivo: " + userId);
        System.out.println("EditarProductosOrdenDto recibido: " + editarProductosOrdenDto);

        return gestionOrdenesService.editarOrden(userId, editarProductosOrdenDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Confirmar orden de compra",
            description = """
                    Confirma la orden del usuario cambiando su estado de PENDIENTE a CONFIRMADA.
                    
                    **Funcionalidad:**
                    - Cambia el estado de la orden a CONFIRMADA
                    - Solo permite confirmar órdenes en estado PENDIENTE
                    - Genera notificación por email al usuario
                    - Una vez confirmada, la orden no puede ser modificada
                    
                    **Validaciones:**
                    - La orden debe existir
                    - La orden debe estar en estado PENDIENTE
                    - La orden debe tener al menos un producto
                    
                    **Nota:** Después de confirmar, no se pueden agregar, editar o eliminar productos.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden confirmada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MostrarInformacionOrdenDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La orden ya está confirmada o no se puede confirmar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada para el usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/confirmarorden")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> confirmarOrden(
            @Parameter(description = "ID del usuario autenticado", required = true)
            @RequestHeader("X-Auth-User-Id")Long userId) {
        return estadoOrdenService.confirmarOrden(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Eliminar un producto de la orden",
            description = """
                    Elimina un producto específico de la orden del usuario.
                    
                    **Funcionalidad:**
                    - Elimina el producto de la orden completamente
                    - Devuelve el stock al inventario automáticamente
                    - Recalcula los totales de la orden
                    - Solo permite eliminar de órdenes en estado PENDIENTE
                    
                    **Validaciones:**
                    - El producto debe existir en la orden
                    - La orden debe estar en estado PENDIENTE
                    - El productoId debe ser válido (mayor a 0)
                    
                    **Nota:** Esta operación es irreversible. Si desea volver a agregar el producto,
                    debe usar el endpoint de crear/actualizar orden.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MostrarInformacionOrdenDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Producto no existe en la orden o estado no permitido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/eliminarproducto/{productoId}")
    public Mono<ResponseEntity<MostrarInformacionOrdenDto>> eliminarProductoDeOrden(
            @Parameter(description = "ID del usuario autenticado", required = true)
            @RequestHeader("X-Auth-User-Id") Long userId,
            @Parameter(description = "ID del producto a eliminar de la orden", required = true, example = "1")
            @PathVariable Long productoId) {

        return gestionOrdenesService.eliminarProductoDeOrden(userId, productoId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
