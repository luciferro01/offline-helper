package com.mohil_bansal.search_service.dto;

public class ProductOfferingDto {
    private String productOfferingId;  // Unique ID for this product offering
    private Long productId;  // Reference to the product ID
    private String productName;  // Product name
    private Long sellerId;
    private String sellerName;  // Seller name (e.g., "EasyBuy")
    private double price;  // Price from the seller
    private int stock;  // Available stock from the seller
    private double sellerRating;  // Seller's rating (out of 5)
    private int productsSoldCount;  // Number of products sold by the seller
    private int totalProductsOffered;  // Total number of different products offered by the seller
    private int productReviews;  // Number of customer reviews for the product
    private String category;  // Category of the product (e.g., "Smartphones")

    // Getters and Setters
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

    public double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public int getProductsSoldCount() {
        return productsSoldCount;
    }

    public void setProductsSoldCount(int productsSoldCount) {
        this.productsSoldCount = productsSoldCount;
    }

    public int getTotalProductsOffered() {
        return totalProductsOffered;
    }

    public void setTotalProductsOffered(int totalProductsOffered) {
        this.totalProductsOffered = totalProductsOffered;
    }

    public int getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(int productReviews) {
        this.productReviews = productReviews;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}