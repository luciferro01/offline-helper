package com.example.searchKafkaConsumer.service;

import com.example.searchKafkaConsumer.dto.ProductOfferingDto;
import com.example.searchKafkaConsumer.event.ProductOfferingUpdateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductOfferingConsumerService {

    @KafkaListener(topics = "product-updates", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductOfferingUpdateEvent(String event) {
        ProductOfferingDto productOfferingDto = new ProductOfferingDto();
        log.info("Received Product Offering Update Event: {}", event);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ProductOfferingUpdateEvent updateEvent = objectMapper.readValue(event, ProductOfferingUpdateEvent.class);
            log.info("Product Offering Update Event: {}", updateEvent.getOperationType());
            log.info("Product Offering DTO: {}", updateEvent.getProductOfferingDto());
            productOfferingDto = updateEvent.getProductOfferingDto();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        log.info(productOfferingDto.toString());
    }
}
