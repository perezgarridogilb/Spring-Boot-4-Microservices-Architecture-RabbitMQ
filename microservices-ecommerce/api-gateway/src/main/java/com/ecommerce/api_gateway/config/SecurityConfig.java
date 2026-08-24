package com.ecommerce.api_gateway.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ecommerce.api_gateway.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 
 * 
 1. El Service Provider (SecurityServiceProvider.php)
Es el equivalente a tu @Configuration. Registra los middlewares en el pipeline de la aplicación.

PHP
namespace Ecommerce\ApiGateway\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Routing\Router;
use Ecommerce\ApiGateway\Http\Middleware\GatewaySecurityMiddleware;

class SecurityServiceProvider extends ServiceProvider
{
    public function boot(Router $router): void
    {
        // Publicar la configuración del paquete si es necesario
        $this->publishes([
            __DIR__ . '/../../config/gateway-security.php' => config_path('gateway-security.php'),
        ], 'config');

        // Registrar el middleware de seguridad del Gateway
        $router->aliasMiddleware('gateway.auth', GatewaySecurityMiddleware::class);
    }

    public function register(): void
    {
        $this->mergeConfigFrom(
            __DIR__ . '/../../config/gateway-security.php',
            'gateway-security'
        );
    }
}
2. El Middleware de Seguridad (GatewaySecurityMiddleware.php)
Es el equivalente a tu SecurityWebFilterChain, donde manejas la exención de CSRF (implícito en APIs), rutas públicas (como eureka/**) y la validación de tokens JWT (oauth2ResourceServer).

PHP
namespace Ecommerce\ApiGateway\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Illuminate\Support\Facades\Auth;

class GatewaySecurityMiddleware
{
    public function handle(Request $request, Closure $next): Response
    {
        // Equivalent to pathMatchers("eureka/**").permitAll()
        if ($request->is('eureka/*') || $request->is('eureka')) {
            return $next($request);
        }

        // Equivalent to .anyExchange().authenticated() + .oauth2ResourceServer(jwt -> {})
        if (!Auth::guard('api')->check()) {
            return response()->json([
                'message' => 'Unauthenticated / Invalid JWT Token'
            ], Response::HTTP_UNAUTHORIZED);
        }

        return $next($request);
    }
}
3. Configuración del Paquete (config/gateway-security.php)
Para imitar la flexibilidad de Spring Security, puedes definir las rutas exentas en un archivo de configuración del paquete:

PHP
return [
    // Equivalente a pathMatchers().permitAll()
  
    'public_routes' => [
        'eureka/*',
        'health',
    ],
];
 * 
 * 
 * SecurityConfig
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.csrf(
                ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeEchangeSpec -> authorizeEchangeSpec

                        .pathMatchers(HttpMethod.GET, "/api/v1/product/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/inventory/**").permitAll()
                        .pathMatchers("/api/v1/product/**").hasRole(Role.ADMIN.name())
                        .pathMatchers("/api/v1/inventory/**").hasRole(Role.ADMIN.name())


                        .pathMatchers(HttpMethod.POST, "/api/v1/order").hasRole(Role.USER.name())

                        .pathMatchers(HttpMethod.GET, "/api/v1/order/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/order/**").hasRole(Role.ADMIN.name())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/order/**").hasRole(Role.ADMIN.name())

                        .anyExchange().authenticated()
                    ).oauth2ResourceServer(oath2 -> oath2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(reactiveJwtAuthenticationConverterAdapter()))

                );
        return serverHttpSecurity.build();
    }

    private ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverterAdapter(){
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter( jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }

            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            // if (roles == null) {
            //     return List.of();
            // }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
