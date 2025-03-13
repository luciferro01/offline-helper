package com.mohil_bansal.search_service.dto;

import lombok.Data;

@Data
public class ResponseProductOfferingDto {
    private String id;
    private String productOfferingId;
    private Long productId;
    private String productName;
    private Long sellerId;
    private String sellerName;
    private double price;
    private int stock;
    private String category;
    private String productImage;
    private double productRating;

    public ResponseProductOfferingDto() {
    }

    public ResponseProductOfferingDto(String id, String productOfferingId, Long productId, String productName, Long sellerId, String sellerName, double price, int stock, String category, String productImage, double productRating) {
        this.id = id;
        this.productOfferingId = productOfferingId;
        this.productId = productId;
        this.productName = productName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.productImage = productImage;
        this.productRating = productRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public void setProductOfferingId(String productOfferingId) {
        this.productOfferingId = productOfferingId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public double getProductRating() {
        return productRating;
    }

    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }
}
