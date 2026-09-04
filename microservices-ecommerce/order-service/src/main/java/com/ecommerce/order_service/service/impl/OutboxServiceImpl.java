package com.ecommerce.order_service.service.impl;

import java.time.LocalDateTime;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;
import com.ecommerce.order_service.model.OutboxRepository;
import com.ecommerce.order_service.service.OutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOrderPlacedEvent(OrderPlacedEvent event) {
        try {
            // Equivalente a objectMapper.writeValueAsString(event)
            // $payload = json_encode($event);
            String payload = objectMapper.writeValueAsString(event);
            // Conceptualmente es similar a la tabla de cola de trabajo (jobs) de Laravel
            // Es un evento de dominio guardado en la misma transacción de base de datos
            // donde se creó el registro principal
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(event.orderNumber())
                    .type("ORDER_PLACED")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();

            outboxRepository.save(outboxEvent);
            log.info("💾 Evento asegurado en Outbox: {}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
