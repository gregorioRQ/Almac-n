package com.pola.api_inventario.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.models.UserResponse;
import com.pola.api_inventario.rest.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/rol/proveedor/{id}")
    public ResponseEntity<UserResponse> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerUsuarioPorId(id));
    }

    @DeleteMapping("/rol/proveedor/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuarioPorId(id);
        return ResponseEntity.ok("Usuario eliminado.");
    }

    @PostMapping("/rol/proveedor")
    public ResponseEntity<?> actualizarRollaProveedor(@RequestBody ProveedorDto proveedorDto) {
        userService.actualizarRollaProveedor(proveedorDto);
        return ResponseEntity.ok("Usuario actualizado.");
    }
}
