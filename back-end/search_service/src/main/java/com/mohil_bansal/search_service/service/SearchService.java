package com.mohil_bansal.search_service.service;

import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface SearchService {
    List<ProductOffering> search(String query);

    List<ProductOffering> searchByProductName(String productName);

    PageImpl<ProductOffering> findByProductNameContaining(String keyword, int page, int size);

    ProductOfferingDto create(ProductOfferingDto productOfferingDto);

    void deleteAll();
}
