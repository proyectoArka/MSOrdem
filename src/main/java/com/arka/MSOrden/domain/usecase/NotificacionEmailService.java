package com.arka.MSOrden.domain.usecase;

import com.arka.MSOrden.application.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.time.Year;

public class NotificacionEmailService {

    @Value("${lambda.email.url}")
    private String lambdaEmailUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    // Template HTML tal cual como está en el código de carritos abandonados
    private static final String TEMPLATE_ORDEN_ESTADO =
            "<!DOCTYPE html><html><head><title>Actualización de tu Orden - Arka</title><style>body{font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;background-color:#f8f8f8;margin:0;padding:0;}.container{max-width:600px;margin:30px auto;background-color:#ffffff;padding:0;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);}.header{background-color:#3f51b5;color:#ffffff;padding:25px 0;text-align:center;border-radius:10px 10px 0 0;}.header h2{margin:0;font-size:24px;}.content{padding:30px 40px;text-align:center;color:#333;line-height:1.6;}.content p{margin-bottom:15px;font-size:16px;}.info-section{background-color:#f5f5f5;padding:15px;border-radius:8px;margin:20px 0;text-align:left;}.info-section p{margin:8px 0;font-size:14px;color:#555;}.cta-button{background-color:#ff9800;color:#ffffff;padding:15px 35px;text-decoration:none;font-weight:700;border-radius:8px;display:inline-block;margin-top:30px;font-size:18px;transition:background-color 0.3s;}.cta-button:hover{background-color:#f57c00;}.product-list{margin:30px 0;border-top:2px solid #eeeeee;border-bottom:2px solid #eeeeee;padding:15px 0;background-color:#fafafa;}.product-list ul{list-style:none;padding:0;margin:0;text-align:left;}.product-list li{padding:10px 0;border-bottom:1px dashed #e0e0e0;font-size:14px;}.product-list li:last-child{border-bottom:none;}.footer{margin-top:0;padding:20px;font-size:12px;color:#777;text-align:center;background-color:#f4f4f4;border-radius:0 0 10px 10px;}</style></head><body><div class=\"container\"><div class=\"header\"><h2>📦 Actualización de tu Orden</h2></div><div class=\"content\"><p>Hola <strong>[NOMBRE_CLIENTE]</strong>,</p><p>Tu orden ha cambiado de estado a: <strong>[ESTADO_ORDEN]</strong></p><div class=\"info-section\"><p><strong>📞 Teléfono de contacto:</strong> [TELEFONO_CLIENTE]</p><p><strong>📍 Dirección de envío:</strong> [DIRECCION_CLIENTE]</p></div><div class=\"product-list\"><p style=\"font-weight:bold; color:#3f51b5;\">Productos de tu orden:</p><ul>[LISTA_PRODUCTOS]</ul></div><p><strong>Total de tu orden: $[TOTAL_ORDEN]</strong></p><a href=\"[URL_SEGUIMIENTO]\" class=\"cta-button\">VER DETALLES DE MI ORDEN</a></div><div class=\"footer\"><p>Si tienes alguna duda, contáctanos. ¡Gracias por elegir Arka!</p><p style=\"margin-top:5px; font-size:10px;\">© [AÑO_ACTUAL] Arka E-commerce</p></div></div></body></html>";

    /**
     * Método que se ejecuta cada vez que cambia el estado de una orden
     * (similar a detectarYNotificarCarritosAbandonados pero sin @Scheduled)
     */
    public Mono<Void> enviarNotificacionCambioEstado(MostrarInformacionOrdenDto ordenInfo) {
        System.out.println("Preparando notificación de cambio de estado para orden #" + ordenInfo.getIdOrden());

        return Mono.fromRunnable(() -> {
            // Convertir ordenInfo a OrdenAbandonada (equivalente a CarritoAbandonado)
            OrdenAbandonada orden = new OrdenAbandonada();
            orden.setNombreCliente(ordenInfo.getNombreUsuario());
            orden.setEmailCliente(ordenInfo.getEmailUsuario());
            orden.setTelefonoCliente(ordenInfo.getNumeroContacto());
            orden.setDireccionCliente(ordenInfo.getDireccionEnvio());
            orden.setNumeroOrden(ordenInfo.getIdOrden());
            orden.setEstadoOrden(ordenInfo.getEstadoOrden());
            orden.setTotalOrden(ordenInfo.getTotalOrden());
            orden.setUrlSeguimiento("http://localhost:8093/api/v1/gateway/ordenes/" + ordenInfo.getIdOrden());

            // Mapear productos
            java.util.List<ProductoAbandonado> productos = new java.util.ArrayList<>();
            if (ordenInfo.getProductos() != null) {
                for (MostrarInformacionProductosDto p : ordenInfo.getProductos()) {
                    ProductoAbandonado pa = new ProductoAbandonado();
                    pa.setNombreProducto(p.getNombreProducto());
                    pa.setCantidad(p.getCantidad());
                    pa.setPrecioUnitario(p.getPrecioUnitario());
                    productos.add(pa);
                }
            }
            orden.setProductos(productos);

            // Construir y enviar el email
            ConstruirJson(orden);
        })
        .doOnNext(result -> System.out.println("Orden preparada para notificar a: " + ordenInfo.getEmailUsuario()))
        .doOnError(e -> System.err.println("Error detectando orden: " + e.getMessage()))
        .then();
    }

    // Construcción del json de orden y llamado a la lambda para enviar el correo
    // (TAL CUAL como está en el código de carritos)
    private void ConstruirJson(OrdenAbandonada orden) {
        String html = TEMPLATE_ORDEN_ESTADO;
        html = html.replace("[NOMBRE_CLIENTE]", orden.getNombreCliente());
        html = html.replace("[ESTADO_ORDEN]", orden.getEstadoOrden());
        html = html.replace("[TELEFONO_CLIENTE]", orden.getTelefonoCliente() != null ? orden.getTelefonoCliente() : "No especificado");
        html = html.replace("[DIRECCION_CLIENTE]", orden.getDireccionCliente() != null ? orden.getDireccionCliente() : "No especificada");
        html = html.replace("[URL_SEGUIMIENTO]", orden.getUrlSeguimiento());
        html = html.replace("[TOTAL_ORDEN]", String.format("%.2f", orden.getTotalOrden()));
        html = html.replace("[AÑO_ACTUAL]", String.valueOf(Year.now().getValue()));

        StringBuilder productosHtml = new StringBuilder();
        if (orden.getProductos() != null) {
            for (ProductoAbandonado p : orden.getProductos()) {
                productosHtml.append(String.format("<li><strong>%s</strong> - Cantidad: %d - $%.2f</li>",
                    p.getNombreProducto(), p.getCantidad(), p.getPrecioUnitario()));
            }
        }
        html = html.replace("[LISTA_PRODUCTOS]", productosHtml.toString());

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setDestination(orden.getEmailCliente());
        emailRequest.setAsunto(generarAsuntoEmail(orden.getEstadoOrden(), orden.getNumeroOrden()));
        emailRequest.setCuerpoMensaje(html);
        emailRequest.setTipoEvento("CAMBIO_ESTADO_ORDEN");

        llamarLambda(emailRequest);
    }

    private String generarAsuntoEmail(String estado, Long numeroOrden) {
        String emoji;
        String mensaje;

        if (estado == null) estado = "";
        String estadoLower = estado.toLowerCase();

        if (estadoLower.contains("confirmado") || estadoLower.contains("confirma")) {
            emoji = "✅";
            mensaje = "¡Tu orden ha sido confirmada!";
        } else if (estadoLower.contains("despachado") || estadoLower.contains("despacho") || estadoLower.contains("enviado")) {
            emoji = "🚚";
            mensaje = "¡Tu orden está en camino!";
        } else if (estadoLower.contains("entregado") || estadoLower.contains("entrega")) {
            emoji = "📦";
            mensaje = "¡Tu orden ha sido entregada!";
        } else {
            emoji = "📋";
            mensaje = "Actualización de tu orden";
        }

        return String.format("%s %s - Orden #%d - Arka", emoji, mensaje, numeroOrden);
    }

    private void llamarLambda(EmailRequest request) {
        try {
            // Realiza la llamada HTTP POST a la URL de tu Lambda
            restTemplate.postForObject(lambdaEmailUrl, request, Void.class);
            System.out.println("Correo enviado con éxito a la Lambda para: " + request.getDestination());
        } catch (Exception e) {
            // Manejo de errores de conexión o de la Lambda
            System.err.println("Error al llamar a la Lambda: " + e.getMessage());
        }
    }

    // Clase interna para representar la orden (equivalente a CarritoAbandonado)
    private static class OrdenAbandonada {
        private String nombreCliente;
        private String emailCliente;
        private String telefonoCliente;
        private String direccionCliente;
        private Long numeroOrden;
        private String estadoOrden;
        private Double totalOrden;
        private String urlSeguimiento;
        private java.util.List<ProductoAbandonado> productos;

        // Getters y setters
        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
        public String getEmailCliente() { return emailCliente; }
        public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
        public String getTelefonoCliente() { return telefonoCliente; }
        public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
        public String getDireccionCliente() { return direccionCliente; }
        public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }
        public Long getNumeroOrden() { return numeroOrden; }
        public void setNumeroOrden(Long numeroOrden) { this.numeroOrden = numeroOrden; }
        public String getEstadoOrden() { return estadoOrden; }
        public void setEstadoOrden(String estadoOrden) { this.estadoOrden = estadoOrden; }
        public Double getTotalOrden() { return totalOrden; }
        public void setTotalOrden(Double totalOrden) { this.totalOrden = totalOrden; }
        public String getUrlSeguimiento() { return urlSeguimiento; }
        public void setUrlSeguimiento(String urlSeguimiento) { this.urlSeguimiento = urlSeguimiento; }
        public java.util.List<ProductoAbandonado> getProductos() { return productos; }
        public void setProductos(java.util.List<ProductoAbandonado> productos) { this.productos = productos; }
    }

    // Clase interna ProductoAbandonado (tal cual como está en el código original)
    private static class ProductoAbandonado {
        private String nombreProducto;
        private int cantidad;
        private double precioUnitario;

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}

