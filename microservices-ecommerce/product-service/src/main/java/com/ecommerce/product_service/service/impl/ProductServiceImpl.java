package com.ecommerce.product_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductResponseDTO createprodduct(ProductRequestDTO requestDTO) {

        Product product = mapper.toProduct(requestDTO);
        Product savedProduct = repository.save(product);
        log.info("Product: {} guardado", savedProduct.getName());

        return mapper.toProductResponseDTO(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getAllsProducts() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponseDTO)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = repository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Product", "id", id)
        );
        return mapper.toProductResponseDTO(product);
    }

    @Override
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequest) {
        Product product = repository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Product", "id", id)
        );
            //product.setName(productRequest.name());

        mapper.updateProductFromRequest(productRequest, product);
        Product updatedProduct = repository.save(product);
        log.info("Product {} actualizado", updatedProduct.getName());

        return mapper.toProductResponseDTO(updatedProduct);
    }

    /*
 * ==================================================================================
 * ECOSISTEMA DE MICROSERVICIOS Y COMUNICACIÓN SÍNCRONA
 * ==================================================================================
 *
 * 1. ARQUITECTURA REAL (Database per Service):
 *    - Este servicio opera exclusivamente sobre la BD de Order (PostgreSQL).
 *    - La gestión de Inventory reside en su propio servicio con MySQL.
 *
 * 2. COMUNICACIÓN SÍNCRONA (Spring 6 HTTP Interfaces):
 *    - Evolución de llamadas manuales con WebClient a una interfaz declarativa.
 *
 * 3. LÓGICA DE NEGOCIO (Flujo Completo):
 *    - Paso 1: Validar Stock e intentar descontar inventario en el servicio remoto.
 *    - Paso 2: Si el descuento es exitoso, confirmar y guardar la Orden localmente.
 *
 * 4. EL RETO DISTRIBUIDO (Inconsistencia de Datos):
 *    - @Transactional SOLO controla el rollback de la BD local (PostgreSQL).
 *    - Si orderRepository.save() falla después de llamar al servicio de inventario,
 *      la Orden NO se creará, pero el Stock en MySQL YA habrá sido descontado.
 *    - Demuestra por qué las transacciones ACID tradicionales no funcionan entre
 *      microservicios distribuidos mediante llamadas HTTP.
 * ==================================================================================
 */

    @Override
    public void deleteProduct(String id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }

        repository.deleteById(id);
                log.info("Product con el id: {} fue eliminado", id);

     
    }



}
