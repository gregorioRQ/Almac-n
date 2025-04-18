package com.pola.api_inventario.rest.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pola.api_inventario.rest.models.Mensaje;
import com.pola.api_inventario.rest.service.ChatMessageService;

@Controller
public class WebSocketController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload String contenido) {
        ObjectMapper mapper = new ObjectMapper();
        String estadoActual = "";
        try {
            JsonNode node = mapper.readTree(contenido);
            estadoActual = node.path("estado").asText();
        } catch (JsonProcessingException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        }

        String remitente = "Usuario";
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setRemitente(remitente);
        mensaje.setEstadoActual(estadoActual);
        mensaje.setTimestamp(LocalDateTime.now());

        chatMessageService.guardarYEnviarMensaje(mensaje);
    }

    @MessageMapping("/chat.editar")
    public void editarMensaje(@Payload String jsonMensaje) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 1. Parsear el JSON entrante del cliente
            JsonNode rootNode = mapper.readTree(jsonMensaje);
            Long id = rootNode.path("id").asLong();
            String estadoNuevo = rootNode.path("estado").asText();

            // 2. Obtener el mensaje existente de la base de datos
            Optional<Mensaje> mensajeOptional = chatMessageService.obtenerMensajePorId(id);
            if (!mensajeOptional.isPresent()) {
                System.out.println("No se encontró el mensaje con ID: " + id);
                return; // Salir si el mensaje no existe
            }

            Mensaje mensaje = mensajeOptional.get();
            mensaje.setEstadoActual(estadoNuevo);
            // 7. Guardar el mensaje actualizado en la base de datos
            chatMessageService.guardarYEnviarMensaje(mensaje);
        } catch (JsonProcessingException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al editar el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/mensajes")
    public ResponseEntity<List<Mensaje>> obtenerMensajes() {
        return ResponseEntity.ok(chatMessageService.obtenerMensajesAnteriores());
    }

    @DeleteMapping("/mensajes")
    public ResponseEntity<?> eliminarMensajes() {
        chatMessageService.eliminarTodosLosMensajes();
        return ResponseEntity.ok("Mensajes eliminados.");
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Cuando un nuevo cliente se conecta, enviamos el historial de mensajes
        // directamente a su cola privada
        List<Mensaje> mensajesAnteriores = chatMessageService.obtenerMensajesAnteriores();
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            for (Mensaje mensaje : mensajesAnteriores) {
                messagingTemplate.convertAndSend("/queue/".concat(sessionId), mensaje);
            }
        }
    }
}
