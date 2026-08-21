package com.ecommerce.order_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(
        @Valid @RequestBody OrderRequest orderRequest,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return orderService.placeOrder(orderRequest, jwt.getSubject());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    // public List<OrderResponse> getAllOrders() {
    //     return orderService.getAllOrders();
    // }
    public List<OrderResponse> getOrders(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        boolean isAdmin = false;
        
        Map<String, Object> realmAccess = jwt.getClaim("realm accesss");

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            isAdmin = roles.stream().anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
        }

        return orderService.getOrders(userId, isAdmin);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @DeleteMapping("/{id}")
        public void deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
