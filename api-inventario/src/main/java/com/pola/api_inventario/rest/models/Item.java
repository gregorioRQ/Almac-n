package com.pola.api_inventario.rest.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.servlet.annotation.ServletSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double pesoPorUnidad; // Ejemplo: 200 kg de arroz a granel.
    private double volumenPorUnidad; // Ejemplo: 500 litros de aceite.
    private Integer cantidadLotes; // Ejemplo: 10 lotes de cajas de frutas.
    private double pesoLote;
    private Integer unidadesPorLote; // Ejemplo: Cada lote contiene 20 unidades.
    private double longitudPorUnidad; // Ejemplo: 100 metros de tela.

    private String contactoProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    @JsonBackReference
    private Proveedor proveedor;

    private Date fechaUltimaEntrada;
    private Date fechaUltimaSalida;

    private String caducidad;
    private String categoria;
    private String nombre;
    private double tempMin;
    private double tempMax;

    private double largo;
    private double ancho;
    private double altura;
    private boolean esFragil;

    private String estado;

    private String ubicacion;
    private Integer cantidadMinimaLotes; // Cantidad mínima de lotes antes de enviar una notificación.

}
