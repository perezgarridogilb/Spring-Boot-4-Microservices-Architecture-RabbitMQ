package com.ecommerce.product_service.service;

import java.util.List;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;

public interface ProductService {
ProductResponseDTO createprodduct(ProductRequestDTO requestDTO);
List<ProductResponseDTO> getAllsProducts();
}
