package com.pola.api_inventario.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.ItemDto;
import com.pola.api_inventario.rest.models.Mensaje;
import com.pola.api_inventario.rest.repositorio.IMensajeDao;

@Service
public class ChatMessageService {
    @Autowired
    private IMensajeDao mensajeRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "chat.messages")
    public void recibirMensajeDeRabbit(String contenido) {
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setRemitente("Servidor RabbitMQ");
        mensaje.setTimestamp(LocalDateTime.now());
        guardarYEnviarMensaje(mensaje);
    }

    public void guardarYEnviarMensaje(Mensaje mensaje) {
        mensajeRepository.save(mensaje);
        messagingTemplate.convertAndSend("/topic/mensajes", mensaje);
    }

    public Optional<Mensaje> obtenerMensajePorId(Long id) {
        return mensajeRepository.findById(id);
    }

    public List<Mensaje> obtenerMensajesAnteriores() {
        return mensajeRepository.findAllByOrderByTimestampAsc();
    }

    public void eliminarTodosLosMensajes() {
        mensajeRepository.deleteAll();
        // Podrías enviar una notificación por WebSocket aquí si es necesario
        messagingTemplate.convertAndSend("/topic/mensajes/borrado", "Los mensajes han sido borrados.");
    }
}
