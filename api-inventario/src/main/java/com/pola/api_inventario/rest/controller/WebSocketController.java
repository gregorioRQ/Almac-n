package com.pola.api_inventario.rest.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.pola.api_inventario.rest.models.ItemDto;
import com.pola.api_inventario.rest.models.Mensaje;
import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.service.ChatMessageService;
import com.pola.api_inventario.rest.service.ItemService;
import com.pola.api_inventario.rest.service.ProveedorService;
import com.pola.api_inventario.rest.service.UserService;
import com.pola.api_inventario.validacion.JsonValidator;

@Controller
public class WebSocketController {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private NotificationController notificationController;

    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload String mensajeJson) {

        try {
            JsonNode mensajeNode = mapper.readTree(mensajeJson);
            JsonValidator validator = new JsonValidator();
            JsonValidator.ValidationResult resultado = validator.validar(mensajeNode);

            if (!resultado.isValid()) {
                // Si hay errores, notificar al cliente
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("mensaje", "Error en la validación de campos");
                errorResponse.put("errores", resultado.getErrores());

                String jsonResponse = mapper.writeValueAsString(errorResponse);
                notificationController.notificarProveedorNoRegistrado(jsonResponse);
            } else {
                Mensaje mensaje = new Mensaje();
                mensaje.setContenido(mensajeJson);
                mensaje.setRemitente((String) resultado.getDatos().get("remitente"));
                mensaje.setEstadoActual((String) resultado.getDatos().get("estado"));
                mensaje.setUbicacion((String) resultado.getDatos().get("ubicacion"));
                mensaje.setTimestamp(LocalDateTime.now());
                chatMessageService.guardarYEnviarMensaje(mensaje);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    @MessageMapping("/chat.editar")
    public void editarMensaje(@Payload String jsonMensaje) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 1. Parsear el JSON entrante del cliente
            JsonNode rootNode = mapper.readTree(jsonMensaje);
            Long id = rootNode.path("id").asLong();
            String estadoNuevo = rootNode.path("estado").asText();

            // verifica que el campo ubicacion no está vacío
            if (estadoNuevo == null || estadoNuevo.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("mensaje", "Error en la validación de campos");
                errorResponse.put("errores", "El campo 'estado actual del ítem' es obligatorio");

                String jsonResponse = mapper.writeValueAsString(errorResponse);
                notificationController.notificarCampoInvalido(jsonResponse);
                return;
            }

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

    @MessageMapping("/chat.guardar")
    public void guardarItem(@Payload String jsonMensaje) {
        ObjectMapper mapper = new ObjectMapper();
        ItemDto itemGuardar = new ItemDto();

        try {
            JsonNode rootNode = mapper.readTree(jsonMensaje);
            JsonNode contenidoNode = rootNode.path("contenido");
            String remitente = rootNode.path("remitente").asText();
            Long idMensaje = rootNode.path("id").asLong();
            String ubicacion = rootNode.path("ubicacion").asText();

            // verifica que el campo ubicacion no está vacío
            if (ubicacion == null || ubicacion.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("mensaje", "Error en la validación de campos");
                errorResponse.put("errores", "El campo ubicación es obligatorio");

                String jsonResponse = mapper.writeValueAsString(errorResponse);
                notificationController.notificarCampoInvalido(jsonResponse);
                return;
            }

            itemGuardar.setPesoPorUnidad(contenidoNode.get("pesoPorUnidad").asDouble());
            itemGuardar.setVolumenPorUnidad(contenidoNode.get("volumenPorUnidad").asDouble());
            itemGuardar.setCantidadLotes(contenidoNode.get("cantidadLotes").asInt());
            itemGuardar.setPesoLote(contenidoNode.get("pesoLote").asDouble());
            itemGuardar.setUnidadesPorLote(contenidoNode.get("unidadesPorLote").asInt());
            itemGuardar.setLongitudPorUnidad(contenidoNode.get("longitudPorUnidad").asDouble());
            itemGuardar.setCaducidad(contenidoNode.get("caducidad").asText());
            itemGuardar.setCategoria(contenidoNode.get("categoria").asText());
            itemGuardar.setNombre(contenidoNode.get("nombre").asText());
            itemGuardar.setTempMin(contenidoNode.get("tempMin").asDouble());
            itemGuardar.setTempMax(contenidoNode.get("tempMax").asDouble());
            itemGuardar.setLargo(contenidoNode.get("largo").asDouble());
            itemGuardar.setAncho(contenidoNode.get("ancho").asDouble());
            itemGuardar.setAltura(contenidoNode.get("altura").asDouble());
            itemGuardar.setEsFragil(contenidoNode.get("esFragil").asBoolean());
            itemGuardar.setCantidadMinimaLotes(contenidoNode.get("cantidadMinimaLotes").asInt());
            itemGuardar.setUbicacion(ubicacion);
            itemGuardar.setNombreComercial(remitente);

            itemService.guardarItem(itemGuardar);

            chatMessageService.eliminarMensajePorId(idMensaje);
            notificationController.notificarEliminacionMensaje(idMensaje.toString());

        } catch (JsonProcessingException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al guardar el item: " + e.getMessage());
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
