package com.mohil_bansal.apigateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/auth/**").permitAll() // Allow unauthenticated access to auth endpoints
                .anyExchange().authenticated()        // All other endpoints require authentication
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String secret = "mohilBansalIsAwesome"; // Match your auth server secret
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // For HS512, key should be at least 512 bits (64 bytes)
        // Pad if necessary
        byte[] paddedKey = Arrays.copyOf(keyBytes, 64);

        SecretKeySpec key = new SecretKeySpec(paddedKey, "HmacSHA512");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}