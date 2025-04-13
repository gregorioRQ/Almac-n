package com.pola.api_inventario.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//configuracion para websockets

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // suscripcion (topic) para mensaje de difucion servidor cliente
        // al suscribirte a este topic recibes los mensajes publicados
        config.enableStompBrokerRelay("/topic", "/queue")
                // ip de rabbitmq
                .setRelayHost("localhost")
                // purto stomp de rabbitmq
                .setRelayPort(61613)
                // usuario
                .setClientLogin("guest")
                // contraseña
                .setClientPasscode("guest");
        // prefijo para mensajes enviados por el cliente ( cliente al servidor )
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    // registra el endpoint para la conexion websocket
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * SockJS es una librería que proporciona una capa de compatibilida para
         * WebSockets. Se usa cuando el cliente o el servidor no pueden establecer una
         * conexión WebSocket directa ya sea por navegadores antiguos o restricciones de
         * firewalls.
         * si websockts no esta permitido socketjs cambia a otros metodos
         * de transporte automaticamente
         */
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }

}
