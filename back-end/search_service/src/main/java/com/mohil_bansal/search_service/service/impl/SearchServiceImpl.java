package com.mohil_bansal.search_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import com.mohil_bansal.search_service.event.ProductOfferingUpdateEvent;
import com.mohil_bansal.search_service.repository.ProductOfferingRepository;
import com.mohil_bansal.search_service.service.RankCalculatorService;
import com.mohil_bansal.search_service.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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

        //TODO: Use SolrQuery to search
//        SolrQuery solrQuery = new SolrQuery();
//            solrQuery.add("q", query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerRank").descending());
        List<ProductOffering> productOfferingsList = new ArrayList();
        Page<ProductOffering> productOfferingsPage = productOfferingRepository.findByProductName(keyword, pageable);


        productOfferingsPage.forEach(productOfferingsList::add);
        return new PageImpl<>(productOfferingsList, pageable, productOfferingsPage.getTotalElements());
    }


    @Override
    @KafkaListener(topics = "product-updates", groupId = "${spring.kafka.consumer.group-id}")
    public ProductOfferingDto update(ProductOfferingDto productOfferingDto) {
        log.info("Received Product Offering Update Event: {}", productOfferingDto);
//        try{
//            ObjectMapper objectMapper = new ObjectMapper();
//            ProductOfferingUpdateEvent updateEvent = objectMapper.readValue(event, ProductOfferingUpdateEvent.class);
//            log.info("Product Offering Update Event: {}", updateEvent.getOperationType());
//            log.info("Product Offering DTO: {}", updateEvent.getProductOfferingDto());
//            productOfferingDto = updateEvent.getProductOfferingDto();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        log.info(productOfferingDto.toString());
        return null;
    }

    @KafkaListener(topics = "product-updates", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductOfferingUpdateEvent(String event) {
        ProductOfferingDto productOfferingDto = new ProductOfferingDto();
        log.info("Received Product Offering Update Event: {}", event);
//        try{
//            ObjectMapper objectMapper = new ObjectMapper();
//            ProductOfferingUpdateEvent updateEvent = objectMapper.readValue(event, ProductOfferingUpdateEvent.class);
//
//            //Logs
//            log.info("Product Offering Update Event: {}", updateEvent.getOperationType());
//            log.info("Product Offering DTO: {}", updateEvent.getProductOfferingDto());
//
//            //Calling respective Methods
//            if(updateEvent.getOperationType() == "CREATE"){
//                create(updateEvent.getProductOfferingDto());
//            }else if(updateEvent.getOperationType() == "UPDATE"){
//                update(updateEvent.getProductOfferingDto());
//            }
//            productOfferingDto = updateEvent.getProductOfferingDto();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        log.info(productOfferingDto.toString());
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
