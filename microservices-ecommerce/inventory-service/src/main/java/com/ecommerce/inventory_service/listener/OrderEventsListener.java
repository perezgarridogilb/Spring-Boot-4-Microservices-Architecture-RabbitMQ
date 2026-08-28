package com.ecommerce.inventory_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.event.OrderCancelledEvent;
import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import com.ecommerce.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "inventory-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {

        log.info("Evento recibido en inventario para orden {}", event.orderNumber());

        // event.items().forEach(item -> {

        try {

            boolean allProductsInStock = event.items().stream()
                    .allMatch(item -> inventoryService.isInStock(item.sku(), item.quantity()));

            if (!allProductsInStock) {
                cancelOrder(event, "Stock insuficiente en uno o más productos");
                return;
            }

            event.items().forEach(item -> {
                inventoryService.reduceStock(item.sku(), item.quantity());
            });

            rabbitTemplate.convertAndSend("order-events", "order.confirmed", event);

            // inventoryService.reduceStock(item.sku(), item.quantity());
            log.info("Stock descontado para Orden número {}", event.orderNumber());

            log.info("");
        } catch (Exception e) {
            log.info("Error técnico : {}", e.getMessage());
            cancelOrder(event, "Error técnico en el procesamiento de inventario");

            // TODO: handle exception
        }

        // });
    }

    private void cancelOrder(
            OrderPlacedEvent event,
            String reason
        ) {
        OrderCancelledEvent cancelledEvent = new OrderCancelledEvent(
                event.orderNumber(), event.email(), reason);

        rabbitTemplate.convertAndSend("order-events", "order.cancelled", cancelledEvent);
    }

}
