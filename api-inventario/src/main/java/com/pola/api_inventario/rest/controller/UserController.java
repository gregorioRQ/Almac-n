package com.pola.api_inventario.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping("/rol/proveedor")
    public ResponseEntity<?> actualizarRollaProveedor(@RequestBody ProveedorDto proveedorDto) {
        userService.actualizarRollaProveedor(proveedorDto);
        return ResponseEntity.ok("Usuario actualizado.");
    }
}
