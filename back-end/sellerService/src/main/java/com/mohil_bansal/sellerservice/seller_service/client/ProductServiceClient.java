package com.mohil_bansal.sellerservice.seller_service.client;

import com.mohil_bansal.sellerservice.seller_service.config.FeignConfig;
import com.mohil_bansal.sellerservice.seller_service.dto.ProductDto;
import com.mohil_bansal.sellerservice.seller_service.utils.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SERVICE-PRODUCT",configuration = FeignConfig.class) // Eureka service ID of Product Service
public interface ProductServiceClient {

    @GetMapping("/products/{productId}") // Relative path to Product Service endpoint
    CommonResponse<ProductDto> getProductById(@PathVariable("productId") Long productId); // Method to get product details by ID
}
