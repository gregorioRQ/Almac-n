package com.pola.api_inventario.rest.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    private String type; // "NUEVA PARTE", "PETICION DE COMPRA", ETC.
    private String message;
    private String sender;
    private String recipient;
    private Long partId;
    private Date timestamp;

}
