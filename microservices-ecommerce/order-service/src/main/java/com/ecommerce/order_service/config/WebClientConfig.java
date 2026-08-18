package com.ecommerce.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.ecommerce.order_service.service.client.InventoryClient;

// Equivalente a registrar configuraciones en AppServiceProvider.php
@Configuration 
public class WebClientConfig {

    // Configura la base del cliente HTTP (En Laravel: un PendingRequest configurado con Http::baseUrl)
    @Bean 
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
        .baseUrl("http://localhost:8082"); // Laravel: Http::baseUrl('http://localhost:8082')
    }


/*

class AppServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        // Equivale al @Bean de Spring que construye el client desde la interfaz
        $this->app->singleton(InventoryClientInterface::class, function ($app) {
            $pendingRequest = Http::baseUrl('http://localhost:8082')
                ->acceptJson()
                ->timeout(5);

            return new HttpInventoryClient($pendingRequest);
        });
    }
}
            return new HttpInventoryClient($pendingRequest);
*/    @Bean 
    public InventoryClient inventoryClient(WebClient.Builder webClientBuilder) {
        // Construye el WebClient a partir del builder configurado (Laravel: Http::baseUrl + acceptJson)
        WebClient webClient = webClientBuilder.build();
        
        // Adapta la instancia del cliente HTTP para ser usada por la fábrica proxy
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        
        // Construye el generador dinámico de peticiones basadas en la interfaz Java
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        
        // Retorna la implementación concreta mapeada a la interfaz (Laravel: return new HttpInventoryClient($pendingRequest))
        return factory.createClient(InventoryClient.class);
    }
}