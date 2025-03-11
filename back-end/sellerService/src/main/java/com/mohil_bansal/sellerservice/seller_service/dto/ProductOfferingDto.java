package com.mohil_bansal.sellerservice.seller_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
