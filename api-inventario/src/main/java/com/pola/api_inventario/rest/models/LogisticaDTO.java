package com.pola.api_inventario.rest.models;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogisticaDTO {
    @NotBlank(message = "El campo departamento no puede estar vacío")
    @Size(message = "El campo departamento no puede tener mas de 15 caracteres", max = 15)
    private String departamento;
    @Size(message = "El campo descripción no pude tener mas de 50 caracteres.", max = 50)
    private String descripcion;
    private Date fechaCreacion;
}
