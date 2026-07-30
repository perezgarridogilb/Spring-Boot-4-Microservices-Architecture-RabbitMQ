package com.ecommerce.product_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "product-db";
    }

    @Override
    public MongoClient mongoClient() {
        // La URI incluye credenciales, host, puerto y authSource
        // Nota: Si corres el app dentro de Docker Compose, cambia 'localhost' por 'mongodb'
        ConnectionString connectionString = new ConnectionString("mongodb://root:password@localhost:27017/product-db?authSource=admin");

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();

        return MongoClients.create(settings);
    }
}