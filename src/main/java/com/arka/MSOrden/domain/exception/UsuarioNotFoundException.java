package com.arka.MSOrden.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario.
 */
public class UsuarioNotFoundException extends DomainException {

    public UsuarioNotFoundException(Long userId) {
        super("Usuario con ID " + userId + " no encontrado");
    }

    public UsuarioNotFoundException(String message) {
        super(message);
    }
}

