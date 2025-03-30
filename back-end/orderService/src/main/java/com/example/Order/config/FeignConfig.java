package com.example.Order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class FeignConfig {

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot's default ObjectMapper

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log full request and response details
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }

    @Slf4j
    public static class FeignRequestInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            log.info("Feign Request: {} {} ", template.method(), template.url());
            template.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // Add Accept: application/json header
            if (template.headers() != null) {
                log.info("Feign Request Headers: {}", template.headers());
            }
            if (template.body() != null) {
                log.info("Feign Request Body: {}", new String(template.body()));
            }
        }
    }
}