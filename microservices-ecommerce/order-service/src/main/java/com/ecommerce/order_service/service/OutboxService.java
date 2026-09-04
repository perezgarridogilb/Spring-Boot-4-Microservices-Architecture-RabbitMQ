package com.ecommerce.order_service.service;

import com.ecommerce.order_service.event.OrderPlacedEvent;

public interface OutboxService {
    void saveOrderPlacedEvent(OrderPlacedEvent event);
}
