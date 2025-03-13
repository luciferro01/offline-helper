package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;
import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;

import java.util.List;

public interface SellerService {
    SellerDto addSeller(SellerDto sellerDto);
//    void updateSeller();
//    void deleteSeller();
    SellerDto getSellerById(Long id);
    List<SellerDto> getAllSellers();
    SellerDto updateSeller(Long sellerId, SellerDto sellerDto);

    ProductOfferingDto getProductOffering(Long productOfferingId);
    List<ProductOfferingDto> getProductOfferingsBySeller(Long sellerId);
    ProductOfferingDto addProductOffering(ProductOfferingDto offeringDto);
    ProductOfferingDto updateProductOffering(Long productOfferingId, ProductOfferingDto offeringDto);
    List<SellerDto> getSellersForProduct(Long productId);
}
