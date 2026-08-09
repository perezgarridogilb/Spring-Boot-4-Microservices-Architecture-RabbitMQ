package com.ecommerce.inventory_service.repository.impl;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Usa preferentemente Spring Transactional

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
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
    public void deleteInventory(Long id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {
        return inventoryRepository.findBySku(sku)
        .map(inventory -> inventory.getQuantity() >= quantity)
        .orElse(false);
    }

    @Override
    public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryMapper.toModel(inventoryRequest);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(savedInventory);
    }

}
