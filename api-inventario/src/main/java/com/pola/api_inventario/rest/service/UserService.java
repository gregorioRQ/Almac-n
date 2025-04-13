package com.pola.api_inventario.rest.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.models.Role;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.models.UserResponse;
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

    public UserResponse obtenerUsuarioPorId(Long id) {
        Optional<User> usuario = userDao.findById(id);
        if (usuario.isEmpty()) {
            throw new EntityNotFoundException(
                    "El usuario con el username no se encuentra registrado.");
        }
        return convertirAUserResponse(usuario.get());
    }

    public void eliminarUsuarioPorId(Long id) {
        Optional<User> usuario = userDao.findById(id);
        if (usuario.isEmpty()) {
            throw new EntityNotFoundException(
                    "El usuario con el username no se encuentra registrado.");
        }
        userDao.delete(usuario.get());
    }

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

    public UserResponse convertirAUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .role(user.getRole().name()) // Convertir el enum Role a String
                .build();
    }
}
