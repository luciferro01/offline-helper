package com.example.Order.client;

import com.example.Order.config.FeignConfig;
import com.example.Order.dto.ProductOfferingDto;
import com.example.Order.utils.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-seller",configuration = FeignConfig.class) // Assuming 'seller-service' is the application name of productOfferingService in Eureka
public interface ProductOfferingServiceClient {

    @GetMapping("/seller/{productOfferingId}") // Path to get product offering in productOfferingService
    ResponseEntity<CommonResponse<ProductOfferingDto>> getProductOffering(@PathVariable("productOfferingId") Long productOfferingId);

    @PutMapping("/seller/offering/{productOfferingId}") // Path to update product offering in productOfferingService
    ResponseEntity<CommonResponse<ProductOfferingDto>> updateProductOffering(@PathVariable("productOfferingId") Long productOfferingId, @RequestBody ProductOfferingDto productOfferingDto);
}