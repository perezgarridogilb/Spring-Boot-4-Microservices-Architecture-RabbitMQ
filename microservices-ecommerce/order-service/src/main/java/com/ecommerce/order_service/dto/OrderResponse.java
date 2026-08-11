package com.ecommerce.order_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Genera getters, setters, equals(), hashCode() y toString() para todos los atributos
@AllArgsConstructor   // Genera un constructor con TODOS los parámetros: (id, sku, quantity)
@NoArgsConstructor    // Genera un constructor VACÍO: (), indispensable para JPA, Hibernate y Jackson (JSON)
@Builder              // Aplica el patrón Builder para crear instancias fluidas
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private List<OrderLineItemsResponse> orderLineItemsList/* orderLineItemsDtoList */;

}
