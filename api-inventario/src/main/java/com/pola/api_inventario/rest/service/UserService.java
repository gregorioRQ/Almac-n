package com.pola.api_inventario.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.models.UserResponse;
import com.pola.api_inventario.rest.repositorio.IuserDao;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private IuserDao userDao;

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

    public List<UserResponse> obtenerTodosLosUsuarios() {
        List<User> users = userDao.findAll();
        return users.stream().map(this::convertirAUserResponse).toList();
    }

    public User obtenerUserPorUsername(String username) {
        return userDao.findByUsername(username);
    }

    public UserResponse convertirAUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .role(user.getRole().name()) // Convertir el enum Role a String
                .proveedor(user.getProveedor().getNombreComercial())
                .build();
    }
}
