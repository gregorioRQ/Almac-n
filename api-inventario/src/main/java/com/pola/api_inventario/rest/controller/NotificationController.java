package com.pola.api_inventario.rest.controller;

import java.util.Date;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import com.pola.api_inventario.rest.models.Notification;
import com.pola.api_inventario.rest.service.ChatMessageService;
import com.pola.api_inventario.rest.models.Item;
import com.pola.api_inventario.rest.models.Mensaje;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;

    @MessageMapping("/logistica")
    @SendTo("/topic/logistica")
    public Mensaje handleItemMessage(Mensaje mensaje) {
        return mensaje;

    }

    // envia el id del mensaje eliminado el cliente subcrito.
    public void notificarEliminacionMensaje(String mensajeId) {
        messagingTemplate.convertAndSend("/topic/mensajes-eliminados", mensajeId);
    }

    // envia el historial de mensajes al cliente cuando este se conecta.
    @MessageMapping("/historialmensajes")
    public void sendInitialMessages() {
        List<Mensaje> historial = messageService.obtenerMensajesAnteriores();
        for (Mensaje m : historial) {
            messagingTemplate.convertAndSend("/topic/mensajes", m);
        }
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

    public void notificarCampoInvalido(String advertencia) {
        Notification notification = Notification.builder()
                .type("ADVERTENCIA")
                .message(advertencia)
                .timestamp(new Date())
                .build();
        messagingTemplate.convertAndSend("/topic/repositor", notification);
    }
}
