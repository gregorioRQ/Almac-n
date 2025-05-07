package com.pola.api_inventario.config;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.pola.api_inventario.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.messaging.MessageDeliveryException;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /*
     * configura rabbitmq como broker de mensajeria.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
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

    /*
     * valida las conecciones websocket entrantes.
     * se ejecuta antes de que se establezca la conexión websocket.
     * StompCommand.CONNECT verifica si el mensaje es un intento de conexión
     * inicial.
     * si el token es válido y tiene el rol correcto: la conexión websocket se
     * establece
     * y el cliente puede enviar/recibir mensajes.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Obtener el token del header
                    List<String> authorization = accessor.getNativeHeader("Authorization");

                    if (authorization != null && !authorization.isEmpty()) {
                        // sacar el token
                        String token = authorization.get(0).substring(7);

                        try {
                            // extraer el username del token
                            String username = jwtUtil.getUsernameFromToken(token);

                            if (username != null) {
                                // Cargar detalles del usuario
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                                if (jwtUtil.isValidToken(token, userDetails)) {
                                    // Extraer los roles del token
                                    List<GrantedAuthority> authorities = jwtUtil.getRolesFromToken(token).stream()
                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                            .collect(Collectors.toList());

                                    // Crear objeto de autenticación
                                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            authorities);

                                    // Establecer la autenticación
                                    accessor.setUser(auth);
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                } else {
                                    throw new IllegalArgumentException("token invalido");
                                }
                            }
                        } catch (Exception e) {
                            throw new MessageDeliveryException("No autorizado: " + e.getMessage());
                        }
                    } else {
                        throw new MessageDeliveryException("Token no proporcionado");
                    }
                }
                return message;
            }
        });

    }

    @Override
    // registra el endpoint para la conexion websocket
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws", "/ws/public", "/ws/proveedor", "/ws/logistica")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }

}
