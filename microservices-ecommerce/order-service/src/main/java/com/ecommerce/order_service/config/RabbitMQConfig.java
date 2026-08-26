package com.ecommerce.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order-events"

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

}
