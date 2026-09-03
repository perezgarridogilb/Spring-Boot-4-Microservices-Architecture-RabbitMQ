package com.ecommerce.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import com.ecommerce.notification_service.event.OrderPlacedEvent;

@Configuration
public class RabbitMQConfig {

    /**
     * serializa y deserializa
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        // Usamos la versión moderna de Jackson para Spring Boot 4
        /*
        Lo inyecta el Jackson2JsonMessageConverter de inventory-service al momento de serializar (cuando haces convertAndSend). 
        El mapper saca el nombre de la clase real del objeto que le pasas
        */
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

        // Permitimos que confíe en nuestros paquetes de eventos
        typeMapper.setTrustedPackages("*");

        // MAPAREAREMOS LAS IDENTIDADES:
        // "Nombre de clase que viene del emisor" -> "Clase local que la recibe"
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // Si viene un 'OrderPlacedEvent' desde Inventario, lo tratamos como 'OrderConfirmedEvent' local
        idClassMapping.put("com.ecommerce.inventory_service.event.OrderPlacedEvent", OrderConfirmedEvent.class);

        // Si viene una cancelación, la mapeamos a nuestra clase local de cancelación
        idClassMapping.put("com.ecommerce.inventory_service.event.OrderCancelledEvent", OrderCancelledEvent.class);

        typeMapper.setIdClassMapping(idClassMapping);
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
/**
     * Crea la cola principal de notificaciones (el buzón).
     * Configura el reenvío hacia el 'notification-dlx' cuando un mensaje falla 
     * o se agotan los reintentos locales.
     * @return Queue
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable("notification-queue")
        .withArgument("x-dead-letter-exchange", "notification-dlx")
        .withArgument("x-dead-letter-routing-key", "notification.dead")
        .build();
    }

    /**
     * recibe el mensaje y decide dónde tiene que almacenarlo
     * @return
     */
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    /**
     * Un mensaje falla en notification-queue y RabbitMQ lo manda a notification-dlq mediante el notification-dlx
     * @return
     */
       @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("notification-dlx");
    } 
    
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("notification-dlq", true);
    } 

        @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("notification.dead");
    }

    /**
     * es el contrato que conecta el exchange con la cola
     * 
     * si llega algo con la etiqueta order.confirmed envía algo a la cola notification-queue
     */
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue notificationQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchange).with("order.cancelled");
    }


}
