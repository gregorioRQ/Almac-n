package com.pola.api_inventario.rest.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.models.Role;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.repositorio.IproveedorDao;
import com.pola.api_inventario.rest.repositorio.IuserDao;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProveedorService {

    @Autowired
    private IproveedorDao proveedorDao;
    @Autowired
    private IuserDao userDao;

    @Transactional
    public Proveedor nuevoProveedor(ProveedorDto proveedorDto) {
        User user = userDao.findByUsername(proveedorDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException(
                    "El usuario con el username: " + proveedorDto.getUsername() + " no existe.");
        }
        user.setRole(Role.PROVEEDOR);

        if (proveedorDao.findByNombreComercial(proveedorDto.getNombreComercial()) != null) {
            throw new EntityNotFoundException(
                    "El proveedor: " + proveedorDto.getNombreComercial() + " ya se encuentra registrado.");
        }

        if (proveedorDao.findByEmail(proveedorDto.getEmail()) != null) {
            throw new IllegalArgumentException("Este email ya se encuentra en uso.");
        }

        if (proveedorDao.findByTelefono(proveedorDto.getTelefono()) != null) {
            throw new IllegalArgumentException("Este telefono ya se encuentra en uso.");
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
        // userDao.save(user);
        proveedorDao.save(nuevoProveedor);
        return nuevoProveedor;
    }

    public boolean existeProveedor(String nombreComercial) {
        return proveedorDao.findByNombreComercial(nombreComercial) != null;
    }

    public ProveedorDto obtenerProveedorPorNombreComercial(String nombreComercial) {
        Proveedor proveedor = proveedorDao.findByNombreComercial(nombreComercial);
        if (proveedor == null) {
            throw new EntityNotFoundException("El proveedor: " + nombreComercial + " no se encuentra registrado");
        }
        return mapProveedorAProveedorDto(proveedor);
    }

    public List<ProveedorDto> obtenerTodosLosProveedores() {
        List<Proveedor> proveedores = proveedorDao.findAll();
        return proveedores.stream().map(p -> mapProveedorAProveedorDto(p)).toList();
    }

    public Proveedor buscarPorUsername(String username) {
        return userDao.findByUsername(username).getProveedor();
    }

    private ProveedorDto mapProveedorAProveedorDto(Proveedor proveedor) {
        ProveedorDto dto = new ProveedorDto();
        dto.setUsername(proveedor.getUser().getUsername());
        dto.setNombreComercial(proveedor.getNombreComercial());
        dto.setTelefono(proveedor.getTelefono());
        dto.setEmail(proveedor.getEmail());
        dto.setDireccion(proveedor.getDireccion());
        dto.setTipoProveedor(proveedor.getTipoProveedor());
        return dto;
    }
}
