package com.mohil_bansal.sellerservice.seller_service.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer totalStock ;
    private Integer totalSold ;
    private Double rating=0.0;
}