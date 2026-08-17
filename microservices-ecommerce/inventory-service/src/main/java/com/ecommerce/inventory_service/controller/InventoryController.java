package com.ecommerce.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

@GetMapping("/{sku}") // <-- Agregado /{sku}
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku") String sku, @RequestParam("quantity") Integer quantity) {
return inventoryService.isInStock(sku, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {
return inventoryService.createInventory(inventoryRequest);
    }

        @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getAllInventory() {
return inventoryService.getAllInventory();
    }

    @PutMapping("/reduce/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public String reduceStock(@PathVariable String sku, @RequestParam Integer quantity) {
        inventoryService.reduceStock(sku, quantity);

        return "Stock reducido exitosamente";
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryRequest inventoryRequest) {
        return inventoryService.updateInventory(id, inventoryRequest);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
