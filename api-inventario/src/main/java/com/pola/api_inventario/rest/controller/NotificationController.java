package com.pola.api_inventario.rest.controller;

import java.util.Date;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pola.api_inventario.rest.models.Notification;
import com.pola.api_inventario.rest.models.Item;
import com.pola.api_inventario.rest.models.Mensaje;
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

    public void notificarProveedorNoRegistrado(String advertencia) {
        Notification notification = Notification.builder()
                .type("ADVERTENCIA")
                .message(advertencia)
                .timestamp(new Date())
                .build();
        messagingTemplate.convertAndSend("/topic/proveedor", notification);
    }

    // Notificar a todos los compradores sobre una nueva parte
    public void notificarItemGuardado(Item producto) {
        Notification notification = Notification.builder()
                .type("INFORME")
                .message("item guardado con éxito.")
                .timestamp(new Date())

                .build();

        messagingTemplate.convertAndSend("/topic/proveedor", notification);
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
