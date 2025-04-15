package com.pola.api_inventario.rest.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pola.api_inventario.rest.models.Notification;
import com.pola.api_inventario.rest.models.Item;
import com.pola.api_inventario.rest.models.ItemDto;
import com.pola.api_inventario.rest.models.Mensaje;
import com.pola.api_inventario.rest.models.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/logistica")
    @SendTo("/topic/logistica")
    public Mensaje handleItemMessage(Mensaje mensaje) {
        return mensaje;

    }

    // Notificar a todos los compradores sobre una nueva parte
    public void notificarNuevoItem(Item producto) {
        Notification notification = Notification.builder()
                .type("NUEVO ITEM")
                .message("Nuevo item disponible")
                .timestamp(new Date())

                .build();

        messagingTemplate.convertAndSend("/topic/logistica", notification);
    }

    public void notificarCantidadBajaDeItem(Item item) {
        Notification notification = Notification.builder()
                .type("ADVERTENCIA")
                .message("Faltante de intem: " + item.getNombre())
                .timestamp(new Date())
                .build();
        messagingTemplate.convertAndSend("/topic/proveedor", notification);
    }
}
