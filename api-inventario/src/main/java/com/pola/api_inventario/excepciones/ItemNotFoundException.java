package com.pola.api_inventario.excepciones;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String mensaje) {
        super(mensaje);
    }
}
