package com.pola.api_inventario.rest.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarRolLogisticaDTO {
    @NotBlank(message = "El nombre de usuario no puede estar en blanco")
    @Size(message = "El username no puede superar los 15 caracteres", max = 15)
    private String username;
    @NotBlank(message = "El departamento de logística no puede estar en blanco")
    @Size(message = "El departamento de logistica no puede superar los 15 caracteres", max = 15)
    private String departamentoLogistica;
}
