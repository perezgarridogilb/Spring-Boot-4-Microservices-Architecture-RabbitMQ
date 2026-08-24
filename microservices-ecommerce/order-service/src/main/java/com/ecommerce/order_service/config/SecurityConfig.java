package com.ecommerce.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactiva CSRF (En Laravel equivale a las rutas en routes/api.php que no usan cookies de sesión)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Definición de permisos en las rutas
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a métricas/salud (En Laravel: Route::get('/health')->withoutMiddleware(...))
                .requestMatchers("/actuator/**").permitAll()
                // Protege el resto de endpoints (En Laravel: Route::middleware('auth:sanctum')->group(...))
                .anyRequest().authenticated() // La REGLA (El qué)
            )

            // 3. Actúa como Resource Server validando Bearer JWTs (En Laravel: middleware('auth:api') / Passport)
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults()) // El MECANISMO (El cómo)
            );

        return http.build();
    }
}