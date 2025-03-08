package com.mohil_bansal.sellerservice.seller_service.dto;

import lombok.*;



@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Double rating;
}