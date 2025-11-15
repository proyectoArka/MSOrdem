// java
package com.arka.MSOrden.infrastructure.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException() { super(); }
    public ProductoNoEncontradoException(String message) { super(message); }
    public ProductoNoEncontradoException(String message, Throwable cause) { super(message, cause); }
}
