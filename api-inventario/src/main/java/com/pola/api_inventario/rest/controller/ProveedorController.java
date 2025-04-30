package com.pola.api_inventario.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.ProveedorDto;
import com.pola.api_inventario.rest.service.ProveedorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proveedor")
@RequiredArgsConstructor
public class ProveedorController {
    @Autowired
    private ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<String> nuevoProveedor(@RequestBody ProveedorDto proveedorDto) {
        proveedorService.nuevoProveedor(proveedorDto);
        return ResponseEntity.ok("Proveedor creado.");
    }

    @GetMapping
    public ResponseEntity<ProveedorDto> obtenerProveedorPorNombreComercial(@RequestParam String nombreComercial) {
        return ResponseEntity.ok(proveedorService.obtenerProveedorPorNombreComercial(nombreComercial));
    }

    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDto>> obtenerTodosLosProveedores() {
        return ResponseEntity.ok(proveedorService.obtenerTodosLosProveedores());
    }

    @GetMapping("/prueba")
    public ResponseEntity<Proveedor> obtenerProveedorPorUsername(@RequestParam String username) {
        return ResponseEntity.ok(proveedorService.buscarPorUsername(username));
    }
}
