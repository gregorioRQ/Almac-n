package com.pola.api_inventario.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.models.UserResponse;
import com.pola.api_inventario.rest.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerUsuarioPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuarioPorId(id);
        return ResponseEntity.ok("Usuario eliminado.");
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UserResponse>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(userService.obtenerTodosLosUsuarios());
    }

    @GetMapping
    public ResponseEntity<User> obtenerUserPorUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.obtenerUserPorUsername(username));
    }
}
