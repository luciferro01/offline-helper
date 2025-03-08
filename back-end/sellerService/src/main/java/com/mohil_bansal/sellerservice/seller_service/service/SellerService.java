package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;

import java.util.List;

public interface SellerService {
    SellerDto addSeller(SellerDto sellerDto);
//    void updateSeller();
//    void deleteSeller();
    SellerDto getSellerById(Long id);
    List<SellerDto> getAllSellers();

    //TOOD: Implement this method
    void getAllSellerProductOfferings();
}
