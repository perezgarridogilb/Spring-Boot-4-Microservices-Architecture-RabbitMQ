package com.ecommerce.order_service.sheduler;

import java.util.List;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;
import com.ecommerce.order_service.service.OutboxService;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageRelayer {

    private static final String EXCHANGE = "order-events";
    private static final String ROUTING_KEY = "order.placed";

    private final RabbitTemplate rabbitTemplate;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    // Ejecuta el método cada 10 segundos, sin importar cuánto tiempo tarde en
    // ejecutarse la tarea.
    @Scheduled(fixedDelay = 10000)
    public void relayMessage() {
        List<OutboxEvent> pendingEvents = outboxService.getPendingEvents();
        if (pendingEvents.isEmpty()) {
            log.info("Relayer: Detectados: {} mensajes pendientes", pendingEvents.size());
            for (OutboxEvent event : pendingEvents) {
                try {
                    
                    OrderPlacedEvent originalEvent = objectMapper.readValue(event.getPayload(), OrderPlacedEvent.class);
                    rabbitTemplate.convertAndSend("order.events", "order.placed", originalEvent);
                                    outboxService.MarkAsProcessed(event.getId());

                } catch (JacksonException e) {
                                log.info("Relayer: Error Jackson: {} {}",event.getAggregateId(), e.getMessage());
                } catch (AmqpException e) {
                                log.info("Relayer: Error: {} {}",event.getAggregateId(), e.getMessage());
                }
                // try {
                    
                // } catch (Exception e) {
                //     // TODO: handle exception
                // }
                // outboxService.MarkAsProcessed(event.getId());

            }
        }

    }
}