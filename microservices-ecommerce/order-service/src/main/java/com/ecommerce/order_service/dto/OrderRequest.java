package com.ecommerce.order_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    // private Long id;
    @NotEmpty(message = "La orden debe contar al menos un item")
    @Valid /** con este nombre viene la llave en el json */
    private List<OrderLineItemsRequest> orderLineItemsList/* orderLineItemsDtoList */;


    @NotBlank(message = "El email es requerido")
@Email(message = "El formato del email no es válido")
private String email;

}
