package com.example.Cart.client;

import com.example.Cart.config.FeignConfig;
import com.example.Cart.dto.ProductOfferingDto;
import com.example.Cart.utils.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-seller",configuration = FeignConfig.class) // Assuming 'seller-service' is the application name of productOfferingService in Eureka
public interface ProductOfferingServiceClient {

    @GetMapping("/seller/{productOfferingId}") // Path to get product offering in productOfferingService
    CommonResponse<ProductOfferingDto> getProductOffering(@PathVariable("productOfferingId") Long productOfferingId);
}