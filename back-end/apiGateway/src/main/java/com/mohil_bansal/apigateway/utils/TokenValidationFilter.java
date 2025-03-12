package com.mohil_bansal.apigateway.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class TokenValidationFilter implements GlobalFilter, Ordered {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // Skip validation for /auth/** endpoints
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        // Extract the Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return setUnauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        // Call the Authorization Server's validation endpoint
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8087/auth/is-authorized") // Updated URL to match your auth service
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    // Token is valid; proceed to the microservice
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    // Log the error for debugging
                    System.err.println("Token validation error: " + e.getMessage());
                    return setUnauthorizedResponse(exchange, "Token validation failed: " + e.getMessage());
                });
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        byte[] bytes = ("{\"error\": \"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1; // High precedence but after security filters
    }
}