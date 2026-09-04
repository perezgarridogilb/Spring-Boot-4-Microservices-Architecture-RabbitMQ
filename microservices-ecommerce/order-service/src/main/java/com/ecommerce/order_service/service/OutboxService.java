package com.ecommerce.order_service.service;

import java.util.List;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;

public interface OutboxService {
    void saveOrderPlacedEvent(OrderPlacedEvent event, boolean isProcessed);
    List<OutboxEvent> getPendingEvents();
    void MarkAsProcessed(Long id);
}
