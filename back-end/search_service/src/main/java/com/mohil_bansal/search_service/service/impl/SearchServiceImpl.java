package com.mohil_bansal.search_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.dto.ResponseProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import com.mohil_bansal.search_service.event.ProductOfferingUpdateEvent;
import com.mohil_bansal.search_service.repository.ProductOfferingRepository;
import com.mohil_bansal.search_service.service.RankCalculatorService;
import com.mohil_bansal.search_service.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductOfferingRepository productOfferingRepository;

    @Autowired
    private RankCalculatorService rankCalculatorService;

    @Override
    public List<ResponseProductOfferingDto> search(String query) {
        List<ProductOffering> offerings = productOfferingRepository
                .findByProductNameContainingOrSellerNameContainingOrCategoryContaining(query, query, query);
        return offerings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<ResponseProductOfferingDto> searchByProductName(String productName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        Page<ProductOffering> productOfferingsPage = productOfferingRepository.findByProductName(productName, pageable);

        List<ResponseProductOfferingDto> dtos = productOfferingsPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productOfferingsPage.getTotalElements());
    }

    @Override
    public PageImpl<ResponseProductOfferingDto> searchByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        Page<ProductOffering> productOfferingsPage = productOfferingRepository.findByCategory(category, pageable);

        List<ResponseProductOfferingDto> dtos = productOfferingsPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productOfferingsPage.getTotalElements());
    }

    @Override
    public PageImpl<ResponseProductOfferingDto> searchBySellerName(String sellerName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        Page<ProductOffering> productOfferingsPage = productOfferingRepository.findBySellerName(sellerName, pageable);

        List<ResponseProductOfferingDto> dtos = productOfferingsPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productOfferingsPage.getTotalElements());
    }

    @Override
    public PageImpl<ResponseProductOfferingDto> searchByAll(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        Page<ProductOffering> productOfferingsPage = productOfferingRepository
                .findByProductNameContainingOrSellerNameContainingOrCategoryContaining(query, query, query, pageable);

        List<ResponseProductOfferingDto> dtos = productOfferingsPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productOfferingsPage.getTotalElements());
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

    @KafkaListener(topics = "product-updates", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductOfferingUpdateEvent(String event) {
        log.info("Received Product Offering Update Event: {}", event);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductOfferingUpdateEvent updateEvent = objectMapper.readValue(event, ProductOfferingUpdateEvent.class);

            log.info("Product Offering Update Event: {}", updateEvent.getOperationType());
            log.info("Product Offering DTO: {}", updateEvent.getProductOfferingDto());

            if ("CREATE".equals(updateEvent.getOperationType())) {
                create(updateEvent.getProductOfferingDto());
            } else if ("UPDATE".equals(updateEvent.getOperationType())) {
                update(updateEvent.getProductOfferingDto());
            }
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage(), e);
        }
    }

    private ProductOfferingDto update(ProductOfferingDto productOfferingDto) {
        ProductOffering existingOffering = productOfferingRepository.findById(productOfferingDto.getId())
                .orElseThrow(() -> new RuntimeException("Product Offering not found"));

        BeanUtils.copyProperties(productOfferingDto, existingOffering);
        productOfferingRepository.save(existingOffering);
        rankCalculatorService.recalculateAllSellerRanks();

        return productOfferingDto;
    }

    @Override
    public void deleteAll() {
        productOfferingRepository.deleteAll();
    }

    private ResponseProductOfferingDto convertToResponseDto(ProductOffering offering) {
        ResponseProductOfferingDto dto = new ResponseProductOfferingDto();
        BeanUtils.copyProperties(offering, dto);
        return dto;
    }
}