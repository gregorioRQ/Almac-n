package com.pola.api_inventario.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.ActualizarRolLogisticaDTO;
import com.pola.api_inventario.rest.models.LogisticaDTO;
import com.pola.api_inventario.rest.service.LogisticaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/logistica")
@RequiredArgsConstructor
public class LogisticaController {
    @Autowired
    private LogisticaService logisticaService;

    @GetMapping("/{id}")
    private ResponseEntity<LogisticaDTO> obtenerLogisticaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(logisticaService.obtenerLogisticaPorId(id));
    }

    @GetMapping("/todos")
    private ResponseEntity<List<LogisticaDTO>> obtenerTodosLogistica() {
        return ResponseEntity.ok(logisticaService.obtenerTodosLogistica());
    }

    @PostMapping("/perfil")
    private ResponseEntity<String> actualizarRolUsuarioLogistica(@Valid @RequestBody ActualizarRolLogisticaDTO dto) {
        logisticaService.actualizarRolUsuarioLogistica(dto);
        return ResponseEntity.ok("Usuario actualizado.");
    }

    @PostMapping
    private ResponseEntity<LogisticaDTO> crearLogistica(@Valid @RequestBody LogisticaDTO dto) {
        return ResponseEntity.ok(logisticaService.crearLogistica(dto));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<String> eliminarLogisticaPorId(@PathVariable Long id) {
        logisticaService.eliminarLogisticaPorId(id);
        return ResponseEntity.ok("Eliminado con exito.");
    }

}
