package com.pola.api_inventario.rest.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.repositorio.IproveedorDao;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProveedorService {

    @Autowired
    private IproveedorDao proveedorDao;

    public Proveedor nuevoProveedor(ProveedorDto proveedorDto, User user) {

        if (proveedorDao.findByNombreComercial(proveedorDto.getNombreComercial()) != null) {
            throw new EntityNotFoundException(
                    "El proveedor: " + proveedorDto.getNombreComercial() + " ya se encuentra registrado.");
        }

        Proveedor nuevoProveedor = Proveedor.builder()
                .user(user)
                .nombreComercial(proveedorDto.getNombreComercial())
                .telefono(proveedorDto.getTelefono())
                .email(proveedorDto.getEmail())
                .direccion(proveedorDto.getDireccion())
                .tipoProveedor(proveedorDto.getTipoProveedor())
                .fechaRegistro(new Date())
                .build();
        proveedorDao.save(nuevoProveedor);
        return nuevoProveedor;
    }

    public void eliminarProveedor(String nombreComercial) {
        Proveedor proveedorEliminar = proveedorDao.findByNombreComercial(nombreComercial);

        if (proveedorEliminar == null) {
            throw new EntityNotFoundException("El proveedor: " + nombreComercial + " no se encuentra registrado");
        }
        proveedorDao.delete(proveedorEliminar);
    }
}
