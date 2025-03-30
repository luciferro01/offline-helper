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
//Testing with this
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class TokenValidationFilter implements GlobalFilter, Ordered {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // Skip validation for /auth/** and /search/** endpoints
        if (path.startsWith("/auth/") || path.startsWith("/search/")) {
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
                .uri("http://localhost:8762/auth/is-authorized")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class) // Parse response as a Map
                .flatMap(responseMap -> {
                    // Extracting userId from the response
                    String userId = extractUserId(responseMap);

                    if (userId != null) {
                        // Add userId as query parameter to the request
//                        ServerHttpRequest modifiedRequest = request.mutate()
//                                .queryParam("userId", userId)
//                                .build();

                        ServerHttpRequest modifiedRequest = request.mutate()
                                .uri(UriComponentsBuilder.fromUri(request.getURI())
                                        .queryParam("userId", Long.parseLong(userId))
                                        .build()
                                        .toUri())
                                .build();
                        // Creating a new exchange with the modified request
                        // Trying might work
                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(modifiedRequest)
                                .build();

                        // Proceed
                        return chain.filter(modifiedExchange);
                    }

                    // If userId not found continue anyway
                    // No, I won't let this happen
//                    return chain.filter(exchange);
                    return setUnauthorizedResponse(exchange, "Token validation failed: " + "User Not authorized");
                })
                .onErrorResume(e -> {
                    System.err.println("Token validation error: " + e.getMessage());
                    return setUnauthorizedResponse(exchange, "Token validation failed: " + e.getMessage());
                });
    }

    private String extractUserId(Map<String, Object> responseMap) {
        if (responseMap != null && responseMap.containsKey("data")) {
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            return dataMap.get("userId").toString();
//            return responseMap.get("userId").toString();
        }
        return null;
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