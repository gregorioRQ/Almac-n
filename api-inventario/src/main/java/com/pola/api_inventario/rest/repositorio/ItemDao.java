package com.pola.api_inventario.rest.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pola.api_inventario.rest.models.Item;

public interface ItemDao extends JpaRepository<Item, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE Items SET cantidad = cantidad - :cantidad_a_descontar WHERE id = :id", nativeQuery = true)
    void descontarCantidad(
            @Param("nombre") String nombre,
            @Param("cantidad_a_descontar") int cantidadADescontar);

    @Query(value = "SELECT nombre FROM Items WHERE nombre = :nombre", nativeQuery = true)
    Integer obtenerCantidad(@Param("nombre") String nombre);

    default Integer descontarCantidadYRetornarRestante(String nombre, int cantidadADescontar) {
        descontarCantidad(nombre, cantidadADescontar);
        return obtenerCantidad(nombre);
    }
}
