package com.pola.api_inventario.rest.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.models.Role;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.repositorio.IuserDao;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private IuserDao userDao;
    @Autowired
    private ProveedorService proveedorService;

    /*
     * al editar el perfil del usuario a proveedor ya se estaria
     * creando un nuevo registro para la entidad proveedor.
     */

    public void actualizarRollaProveedor(ProveedorDto proveedorDto) {
        User user = userDao.findByUsername(proveedorDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException(
                    "El usuario con el username: " + proveedorDto.getUsername() + " no existe.");
        }
        user.setRole(Role.PROVEEDOR);

        Proveedor nuevoProveedor = proveedorService.nuevoProveedor(proveedorDto, user);
        user.setProveedor(nuevoProveedor);
        // proveedorDao.save(proveedorExistente);
        userDao.save(user);
    }
}
