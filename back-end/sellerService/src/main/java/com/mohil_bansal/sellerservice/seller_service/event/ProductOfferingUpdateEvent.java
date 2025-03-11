package com.mohil_bansal.sellerservice.seller_service.event;

import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;

public class ProductOfferingUpdateEvent {
    private String operationType; // "CREATE" or "UPDATE"
    private ProductOfferingDto productOfferingDto;

    public ProductOfferingUpdateEvent() {
    }

    public ProductOfferingUpdateEvent(String operationType, ProductOfferingDto productOfferingDto) {
        this.operationType = operationType;
        this.productOfferingDto = productOfferingDto;
    }

    // Getters and Setters
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public ProductOfferingDto getProductOfferingDto() {
        return productOfferingDto;
    }

    public void setProductOfferingDto(ProductOfferingDto productOfferingDto) {
        this.productOfferingDto = productOfferingDto;
    }
}