package com.ecommerce.inventory_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_inventory")
@Getter
@Setter
// Genera un constructor con TODOS los campos en el orden declarado.
// EJEMPLO DE USO:
// Inventory item = new Inventory(1L, "SKU-99", 100);
@AllArgsConstructor

// Genera el constructor VACÍO (sin parámetros).
// INDISPENSABLE para JPA/Hibernate, ya que lo necesita para instanciar la entidad al consultar la BD.
// EJEMPLO DE USO:
// Inventory item = new Inventory();
@NoArgsConstructor

// Implementa el patrón Builder para crear la entidad de forma fluida (ideal para pruebas unitarias o DTOs).
// EJEMPLO DE USO:
// Inventory item = Inventory.builder()
//                      .sku("SKU-99")
//                      .quantity(100)
//                      .build();
@Builder
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private Integer quantity;

}
