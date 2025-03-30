package com.mohil_bansal.search_service.dto;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductOfferingDto {
    private String id;
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
    private int productReviews;  // (MisInterpret) Current -> (Old -- Number of customer reviews for the product) ((New -- Avg. Rating for that product))
    private String category;  // Category of the product (e.g., "Smartphones")

    private String productImage;
    private double productRating;

    public void setId(String id) {
        this.id = id;
    }

    public void setProductOfferingId(String productOfferingId) {
        this.productOfferingId = productOfferingId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public void setProductsSoldCount(int productsSoldCount) {
        this.productsSoldCount = productsSoldCount;
    }

    public void setTotalProductsOffered(int totalProductsOffered) {
        this.totalProductsOffered = totalProductsOffered;
    }

    public void setProductReviews(int productReviews) {
        this.productReviews = productReviews;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }
}