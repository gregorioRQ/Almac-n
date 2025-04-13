package com.pola.api_inventario.excepciones;

public class CantidadInsuficienteException extends RuntimeException {
    public CantidadInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
