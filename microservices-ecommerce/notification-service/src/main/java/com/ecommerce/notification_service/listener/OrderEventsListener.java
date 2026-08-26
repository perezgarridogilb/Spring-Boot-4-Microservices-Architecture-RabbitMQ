package com.ecommerce.notification_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecommerce.notification_service.event.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {


 @RabbitListener(queues = "notification-queue")   
 public void handleOrderPlacedEvent(OrderPlacedEvent event) {

    log.info("Evento recibido en inventario para orden {}", event.orderNumber());

    event.items().forEach(item -> {

        try {
            log.info(" Enviando correo de confirmación a: {}", event.email())
            ;

            log.info("✅ Correo enviado exitosamente para la orden: {}", item.sku(), item.quantity());

            log.info("");
        } catch (Exception e) {
            log.error("Error al descontar stock para SKU {}: {}", item.sku(), e.getMessage());
        }

    });
 }

}
