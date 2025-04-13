package com.pola.api_inventario.rest.models;

import lombok.Data;

@Data
public class DescuentoDto {
    private Long idItem;
    private Integer cantidadLotes; // Ejemplo: 10 lotes de cajas
}
