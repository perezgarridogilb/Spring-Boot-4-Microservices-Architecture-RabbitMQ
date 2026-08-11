package com.ecommerce.order_service.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    public final String resourceName;
        public final String fieldName;
    public final String fieldValue;
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }


    
}
