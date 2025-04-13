package com.pola.api_inventario.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaError {
    private String error;
    private String message;

}
