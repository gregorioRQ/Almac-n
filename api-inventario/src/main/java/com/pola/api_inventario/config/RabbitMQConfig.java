package com.pola.api_inventario.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue chatQueue() {
        // Declara una cola duradera llamada "chat.messages"
        return new Queue("chat.messages", true);
    }
}
