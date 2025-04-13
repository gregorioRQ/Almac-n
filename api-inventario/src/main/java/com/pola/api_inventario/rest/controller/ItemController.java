package com.pola.api_inventario.rest.controller;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pola.api_inventario.rest.models.DescuentoDto;
import com.pola.api_inventario.rest.models.Item;
import com.pola.api_inventario.rest.models.ItemDto;
import com.pola.api_inventario.rest.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/new")
    public ResponseEntity<Item> guardar(@RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemService.guardarItem(itemDto));
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        return new ResponseEntity<>(itemService.obtenerItemPorId(id), HttpStatus.OK);
    }

    @PostMapping("/item/descontar")
    public ResponseEntity<String> descontarItem(@RequestBody DescuentoDto descuentoDto) {
        itemService.aplicarDescuento(descuentoDto);
        return ResponseEntity.ok("descontado");
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<String> eliminarItem(@PathVariable Long id) {
        itemService.eliminarItem(id);
        return ResponseEntity.ok("Item eliminado correctamente");
    }
}
