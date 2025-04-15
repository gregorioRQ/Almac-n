package com.pola.api_inventario.rest.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.pola.api_inventario.rest.models.Mensaje;
import com.pola.api_inventario.rest.service.ChatMessageService;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class WebSocketController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload String contenido) {
        // Cuando un cliente envía un mensaje a /app/chat.enviar
        // Creamos el mensaje y lo enviamos al servicio para guardar y propagar

        // Necesitamos obtener el remitente (podría venir del contexto de la sesión)
        String remitente = "Usuario"; // Reemplazar con la lógica real para obtener el remitente

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setRemitente(remitente);
        mensaje.setTimestamp(LocalDateTime.now());

        chatMessageService.guardarYEnviarMensaje(mensaje); // El servicio ahora también envía por WebSocket
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
