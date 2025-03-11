package com.mohil_bansal.search_service.service.impl;

import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import com.mohil_bansal.search_service.repository.ProductOfferingRepository;
import com.mohil_bansal.search_service.service.RankCalculatorService;
import com.mohil_bansal.search_service.service.SearchService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductOfferingRepository productOfferingRepository;

    @Autowired
    private RankCalculatorService rankCalculatorService;

    @Override
    public List<ProductOffering> search(String query) {
        return productOfferingRepository.findByProductNameContainingOrSellerNameContainingOrCategoryContaining(query, query, query);
    }

    @Override
    public List<ProductOffering> searchByProductName(String productName) {
        return productOfferingRepository.findByProductName(productName);
    }


    public PageImpl<ProductOffering> findByProductNameContaining(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        List<ProductOffering> productOfferingsList = new ArrayList();
        Page<ProductOffering> productOfferingsPage =  productOfferingRepository.findByProductName(keyword, pageable);

        productOfferingsPage.forEach(productOfferingsList::add);
        return new PageImpl<>(productOfferingsList, pageable, productOfferingsPage.getTotalElements());

    }

    @Override
    public ProductOfferingDto create(ProductOfferingDto productOfferingDto) {
        ProductOffering productOffering = new ProductOffering();
        BeanUtils.copyProperties(productOfferingDto, productOffering);
        String id = productOffering.getProductOfferingId() + "-" + productOffering.getProductId();
        productOffering.setId(id);
        productOfferingRepository.save(productOffering);
        rankCalculatorService.recalculateAllSellerRanks();
        return productOfferingDto;
    }

    @Override
    public void deleteAll() {
        productOfferingRepository.deleteAll();
    }
}
