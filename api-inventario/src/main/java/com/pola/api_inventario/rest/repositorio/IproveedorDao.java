package com.pola.api_inventario.rest.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pola.api_inventario.rest.models.Proveedor;

public interface IproveedorDao extends JpaRepository<Proveedor, Long> {
    Proveedor findByNombreComercial(String nombreComercial);
}
