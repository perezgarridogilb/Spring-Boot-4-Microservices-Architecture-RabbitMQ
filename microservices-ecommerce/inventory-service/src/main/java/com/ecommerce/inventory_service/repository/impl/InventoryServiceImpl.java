package com.ecommerce.inventory_service.repository.impl;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Usa preferentemente Spring Transactional

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
// CORRECTO: Tipo de dato + Nombre de la variable
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
        boolean exists = inventoryRepository.existsBySku(inventoryRequest.getSku());
        if (exists) {
            throw new RuntimeException("El inventario para el SKU " + inventoryRequest.getSku()
        + " ya existe" );
        }
        Inventory inventory = inventoryMapper.toModel(inventoryRequest);
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("inventario creado para el SKU: {}", savedInventory.getSku() );
        return inventoryMapper.toResponse(savedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("inventario", "id", id);
        }
        inventoryRepository.deleteById(id);
        log.info("inventario eliminado con ID: {}", id);
    }



    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        
        return inventoryRepository.findAll().stream()
        .map(inventoryMapper::toResponse)
        .toList();
    }

    @Override
    public boolean isInStock(String sku, Integer quantity) {
        return inventoryRepository.findBySku(sku)
        .map(inventory -> inventory.getQuantity() >= quantity)
        .orElse(false);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("inventory", "id", id)
        );
        inventory.setSku(inventoryRequest.getSku());
        inventory.setQuantity(inventoryRequest.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("inventario actualizado para el ID: {}", id);
        return inventoryMapper.toResponse(savedInventory);
    }

}
