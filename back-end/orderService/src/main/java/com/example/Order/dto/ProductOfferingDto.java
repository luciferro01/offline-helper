package com.example.Order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingDto {

    private Long id;
    private Long sellerId;
    private Long productId;
    private String sellerName;
    private String productName;
    private Double price;
    private Integer stock;
    private Integer sold = 0;
    private Integer rating = null;
    private String productImageUrl; // Replaced JsonNode images with String productImageUrl
}