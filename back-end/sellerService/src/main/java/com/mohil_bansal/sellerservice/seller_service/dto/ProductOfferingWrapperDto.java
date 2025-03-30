package com.mohil_bansal.sellerservice.seller_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingWrapperDto {

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotNull(message = "Price cannot be null")
    private Double price;

    @NotNull(message = "Stock cannot be null")
    private Integer stock=0;
}