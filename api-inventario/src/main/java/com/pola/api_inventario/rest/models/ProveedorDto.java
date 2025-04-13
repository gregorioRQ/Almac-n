package com.pola.api_inventario.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDto {
    private String username;
    private String nombreComercial;
    private String telefono;
    private String email;
    private String direccion;
    private String tipoProveedor;
}
