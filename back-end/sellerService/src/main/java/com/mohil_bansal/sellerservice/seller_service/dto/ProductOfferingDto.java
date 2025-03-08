package com.mohil_bansal.sellerservice.seller_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOfferingDto {

    private Long id;
    private Long sellerId;
    private Long productId;
    private Double price;
    private Integer stock;
}
