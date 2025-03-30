package com.mohil_bansal.sellerservice.seller_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    //private JsonNode images;
    private String imagesUrl;
    // Note: SellerDto is NOT included here, as we are fetching Product from Product Service, not Seller from Seller Service in this client.
}
