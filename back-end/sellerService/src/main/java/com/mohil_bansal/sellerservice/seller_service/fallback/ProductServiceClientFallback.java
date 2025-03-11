package com.mohil_bansal.sellerservice.seller_service.fallback;

import com.mohil_bansal.sellerservice.seller_service.client.ProductServiceClient;
import com.mohil_bansal.sellerservice.seller_service.dto.ProductDto;
import com.mohil_bansal.sellerservice.seller_service.utils.CommonResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClientFallback implements ProductServiceClient {


    @Override
    public CommonResponse<ProductDto> getProductById(Long productId) {
        System.out.println("error");
        return null;
    }
}
