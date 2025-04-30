package com.pola.api_inventario.rest.models;

import com.pola.api_inventario.validacion.ValidItemCantidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidItemCantidad // Validación personalizada
public class ItemDto {

    @Min(value = 0, message = "El peso de lote no puede ser negativo")
    private double pesoLote;

    @Min(value = 0, message = "El volumen de cada unidad por lote no puede ser negativo")
    private double volumenPorUnidad;

    @Min(value = 0, message = "La cantidad de lotes no puede ser negativa")
    private Integer cantidadLotes;

    @Min(value = 0, message = "La cantidad de unidades por lote no puede ser negativa")
    private Integer unidadesPorLote;

    @Min(value = 0, message = "La longitud de lsa unidades por lote no puede ser negativa")
    private double longitudPorUnidad;

    @Min(value = 0, message = "El peso de las unidades por lote no puede ser negativo")
    private double pesoPorUnidad;

    @NotEmpty(message = "El proveedor no puede estar vacío")
    @Size(max = 30, message = "El proveedor no puede tener más de 30 caracteres")
    private String nombreComercial;

    @NotEmpty(message = "El contacto del proveedor no puede estar vacío")
    @Size(max = 30, message = "El contacto del proveedor no puede tener más de 30 caracteres")
    private String contactoProveedor;

    private Date fechaUltimaEntrada;
    private Date fechaUltimaSalida;

    @NotEmpty(message = "La caducidad no puede estar vacía")
    @Size(max = 30, message = "La caducidad no puede tener más de 30 caracteres")
    private String caducidad;

    @NotEmpty(message = "La categoría no puede estar vacía")
    @Size(max = 30, message = "La categoría no puede tener más de 30 caracteres")
    private String categoria;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(max = 30, message = "El nombre no puede tener más de 30 caracteres")
    private String nombre;

    private double tempMin;
    private double tempMax;

    private double largo;
    private double ancho;
    private double altura;
    private boolean esFragil;

    @NotEmpty(message = "La ubicación no puede estar vacía")
    @Size(max = 30, message = "La ubicación no puede tener más de 30 caracteres")
    private String ubicacion;

    @NotEmpty(message = "La cantidad minima de lotes no puede esta vacía")
    @Size(message = "este valor no puede tener mas de 20 caracteres")
    private Integer cantidadMinimaLotes;
}
