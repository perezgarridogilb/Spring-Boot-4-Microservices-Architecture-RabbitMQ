package com.ecommerce.order_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest, String userId);
    // List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrders(String userId, boolean isAdmin); // Read All x userId
    OrderResponse getOrderById(Long id);
    void deleteOrder(Long id);
}
