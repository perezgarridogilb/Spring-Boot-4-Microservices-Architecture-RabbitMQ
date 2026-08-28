package com.ecommerce.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    /**
     * serializa y deserializa
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * se encarga de crear la cola (es el buzón)
     * es donde se almacenan los mensajes esperando ser leídos
     * 
     * @return
     */
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification-queue", true);
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
     * es el contrato que conecta el exchange con la cola
     * 
     * si llega algo con la etiqueta order.confirmed envía algo a la cola notification-queue
     */
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchange).with("order.confirmed");
    }


}
