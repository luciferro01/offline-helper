package com.mohil_bansal.search_service.service;

import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.dto.ResponseProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

//
//public interface SearchService {
//    List<ProductOffering> search(String query);
//
//    //    List<ProductOffering> searchByProductName(String productName);
////
////
////    List<ProductOffering> searchByCategory(String category);
//    PageImpl<ProductOffering> searchByProductName(String productName, int page, int size);
//
//    PageImpl<ProductOffering> searchByCategory(String category, int page, int size);
//
//    PageImpl<ProductOffering> searchBySellerName(String sellerName, int page, int size);
//
//    PageImpl<ProductOffering> searchByAll(String query, int page, int size);
//
//
////    PageImpl<ProductOffering> findByProductNameContaining(String keyword, int page, int size);
//
//    ProductOfferingDto create(ProductOfferingDto productOfferingDto);
//
//    ProductOfferingDto update(ProductOfferingDto productOfferingDto);
//
//    void consumeProductOfferingUpdateEvent(String event);
//
//    void deleteAll();
//}

public interface SearchService {

    List<ResponseProductOfferingDto> search(String query);
    PageImpl<ResponseProductOfferingDto> searchByProductName(String productName, int page, int size);
    PageImpl<ResponseProductOfferingDto> searchByCategory(String category, int page, int size);
    PageImpl<ResponseProductOfferingDto> searchBySellerName(String sellerName, int page, int size);
    PageImpl<ResponseProductOfferingDto> searchByAll(String query, int page, int size);
    ProductOfferingDto create(ProductOfferingDto productOfferingDto);

    void deleteAll();

}