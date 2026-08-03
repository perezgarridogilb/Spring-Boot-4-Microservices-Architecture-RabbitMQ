package com.ecommerce.product_service.exception;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Recurso no entontrado");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
        problemDetail.setProperty("Timestamp", Instant.now());

        problemDetail.setProperty("Resorce", ex.getResourceName());

        problemDetail.setProperty("Field", ex.getFieldName());
        problemDetail.setProperty("Value", ex.getFieldValue());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            /* ex.getMessage() */ "La validacion fallo en uno o más campos"
        );

        problemDetail.setTitle("Recurso no entontrado");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
        problemDetail.setProperty("Timestamp", Instant.now());

        
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
            error -> errorMap.put(error.getField(), error.getDefaultMessage())
        );

        problemDetail.setProperty("errors", errorMap);

        return problemDetail;
    }
}
