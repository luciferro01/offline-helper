package com.mohil_bansal.apigateway.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/auth/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .and()
                .authenticationEntryPoint((exchange, ex) -> {
                    System.err.println("JWT Auth Error: " + ex.getMessage());
                    return exchange.getResponse().setComplete();
                })
                .and()
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String secret = "mohilBansalIsAwesome";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // Pad to 32 bytes for HS256
        byte[] paddedKey = Arrays.copyOf(keyBytes, 32);
        SecretKeySpec key = new SecretKeySpec(paddedKey, "HmacSHA256");

        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Default validator that includes timestamp validation
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefault();
        // Custom validator for logging that does not enforce timestamp validation
        OAuth2TokenValidator<Jwt> loggingValidator = token -> {
            System.out.println("Token claims: " + token.getClaims());
            return OAuth2TokenValidatorResult.success();
        };

        // Combine validators. Remove the default validator if you wish to skip timestamps.
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(loggingValidator);
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }
}