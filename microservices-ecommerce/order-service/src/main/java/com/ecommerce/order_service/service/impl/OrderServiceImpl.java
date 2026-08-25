package com.ecommerce.order_service.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WebClient.Builder webClientBuilder;
    private final InventoryClient inventoryClient;

        @Value("${order.enabled:true}")
    private boolean ordersEnabled;

    public CompletableFuture<OrderResponse> fallbackMethod(OrderRequest orderRequest, String userId, Throwable throwable) {

return CompletableFuture.supplyAsync(() -> {
        log.error("🔴 Circuit Breaker activado. Causa {}", throwable.getMessage());
        throw new RuntimeException("El servicio de inventario no responde. Intente mas tarde");
});
        // return new OrderResponse(0L,"00000",Collections.emptyList() );
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<OrderResponse> placeOrder(OrderRequest orderRequest, String userId) {

        
        // return orderMapper.toOrderResponse(savedOrder);
        return CompletableFuture.supplyAsync(() -> {
    long startTime = System.currentTimeMillis();
                    if(!ordersEnabled){
            log.warn("Pedido rechazado: Servicio deshabilitado por configuración.");
            throw new RuntimeException("El servicio de pedidos está actualmente en mantenimiento. Intente más tarde");
        }

        log.info("Colocando nuevo pedido");
        Order order = orderMapper.toOrder(orderRequest);

        order.setUserId(userId);

        for (var item : order.getOrderLineItemsList()) {
            String sku = item.getSku();
            Integer quantity = item.getQuantity();

            try {
                // Boolean inStock = webClientBuilder.build().put()
                //         .uri("http://localhost:8082/api/v1/inventory/reduce/" + sku,
                //                 uriBuilder -> uriBuilder.queryParam("quantity", quantity).build())
                //         .retrieve()
                //         .bodyToMono(Boolean.class)
                //         .block();
                inventoryClient.reduceStock(sku, quantity);
            } catch (Exception e) {
                log.error("Error al reducir stock para el producto {}: {}", sku, e.getMessage());
                throw new IllegalArgumentException(
                        "No se pudo procesar la orden: Stock insuficiente o " + "error de inventario");
            }

            // if (!Boolean.TRUE.equals(inStock)) {
            // throw new IllegalArgumentException("No hay stock disponible para el producto:
            // " + sku);
            // }
        }

        order.setOrderNumber(UUID.randomUUID().toString());

        long totalTime = System.currentTimeMillis() - startTime;

        if (totalTime> 3000) {
            log.warn("⌛️ Timeout detectado internamente ({} ms). Abortando guardado en BD", totalTime);
            throw new RuntimeException("Timeout excedido - rollback manual");
        }

        Order savedOrder = orderRepository.save(order);

        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());
        return orderMapper.toOrderResponse(savedOrder);
        }); 
    }

    // @Override
    // @Transactional(readOnly = true)
    // public List<OrderResponse> getAllOrders() {
    //     return orderRepository.findAll().stream()
    //             .map(orderMapper::toOrderResponse)
    //             .toList();

    // }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Order", "id", String.valueOf(id))
                    );

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden", "id", String.valueOf(id));
        }
        orderRepository.deleteById(id);
        log.info("Orden eliminada. ID: {}", id);
        throw new UnsupportedOperationException("Unimplemented method 'deleteOrder'");
    }

    @Override
        @Transactional(readOnly = true)

    public List<OrderResponse> getOrders(String userId, boolean isAdmin) {
        List<Order> orders;

        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(userId);

        }
        return orders.stream()
        .map(orderMapper::toOrderResponse)
        .toList();
    }

}
