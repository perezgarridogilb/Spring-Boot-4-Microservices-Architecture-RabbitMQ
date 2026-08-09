package com.ecommerce.inventory_service.mapper;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toModel(InventoryRequest inventoryRequest);

    // MapStruct (Java): Equivale a un "Attribute/Accessor" de un Laravel API Resource o un Mutator de Eloquent.
    // Transforma el modelo 'Inventory' a 'InventoryResponse', calculando en tiempo de mapeo
    // la propiedad booleana 'inStock' basándose en si la cantidad es mayor a 0 ($inventory->quantity > 0).
    @Mapping(target = "inStock", expression = "java(inventory.getQuantity() > 0)")
    InventoryResponse toResponse(Inventory inventory);


}
