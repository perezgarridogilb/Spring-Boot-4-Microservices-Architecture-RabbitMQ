package com.ecommerce.inventory_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import com.ecommerce.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {

    private final InventoryService inventoryService;

 @RabbitListener(queues = "inventory-queue")   
 public void handleOrderPlacedEvent(OrderPlacedEvent event) {

    log.info("Evento recibido en inventario para orden {}", event.orderNumber());

    event.items().forEach(item -> {

        try {
            inventoryService.reduceStock(item.sku(), item.quantity());
            log.info("");
        } catch (Exception e) {
            // TODO: handle exception
        }

    });
 }

}
