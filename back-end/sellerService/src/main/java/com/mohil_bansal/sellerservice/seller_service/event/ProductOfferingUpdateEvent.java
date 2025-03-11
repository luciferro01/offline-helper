package com.mohil_bansal.sellerservice.seller_service.event;

import com.mohil_bansal.sellerservice.seller_service.dto.SearchProductOfferingDto;

public class ProductOfferingUpdateEvent {
    private String operationType; // "CREATE" or "UPDATE"
    private SearchProductOfferingDto productOfferingDto;

    public ProductOfferingUpdateEvent() {
    }

    public ProductOfferingUpdateEvent(String operationType, SearchProductOfferingDto productOfferingDto) {
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

    public SearchProductOfferingDto getProductOfferingDto() {
        return productOfferingDto;
    }

    public void setProductOfferingDto(SearchProductOfferingDto productOfferingDto) {
        this.productOfferingDto = productOfferingDto;
    }
}