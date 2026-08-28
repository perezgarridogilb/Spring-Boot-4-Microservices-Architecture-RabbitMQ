package com.ecommerce.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order-events";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Se encarga de crearlo, si no existe lo crea
     * 
     * @return
     */
/**
     * Define la cola de RabbitMQ donde se almacenarán los mensajes.
     * El parámetro 'true' la marca como durable (los mensajes sobrevivirán si el broker se reinicia).
     */
    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue("order-confirmed-queue", true);
    }

    /**
     * Define el Topic Exchange (el enrutador central de eventos).
     * Los productores enviarán sus mensajes a este Exchange y no directamente a la cola.
     */
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    @Bean
    public Queue orderCancelledQueue() {
        return new Queue("order-cancelled-queue", true);
    }

    /**
     * Define el BINDING (la regla de enrutamiento):
     * Vincula la cola 'orderConfirmedQueue' con el 'orderEventsExchange'.
     * 
     * Funcionamiento del Binding:
     * 1. .bind()  -> Especifica la cola destino que recibirá los mensajes.
     * 2. .to()    -> Especifica el Exchange de origen de donde provienen los mensajes.
     * 3. .with()  -> Define la "Routing Key" (filtro). Todo mensaje que llegue al Exchange 
     *                con la clave exacta "order.confirmed" será derivado a esta cola.
     */
    @Bean
    public Binding confirmedBinding(Queue orderConfirmedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder
                .bind(orderConfirmedQueue)
                .to(orderEventsExchange)
                .with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue orderCancelledQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder
                .bind(orderCancelledQueue)
                .to(orderEventsExchange)
                .with("order.cancelled");
    }

}
