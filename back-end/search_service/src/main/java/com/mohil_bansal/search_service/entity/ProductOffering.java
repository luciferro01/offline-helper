package com.mohil_bansal.search_service.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "product_offerings")
public class ProductOffering {

    @Id
    @Field
    @Indexed(type = "long")
    private Long productOfferingId;  // Unique ID for this product offering

    @Field
    @Indexed(type = "long")
    private Long productId;  // Reference to the product ID

    @Field
    @Indexed(name = "productName", type = "string")
    private String productName;  // Product name

    @Field
    @Indexed(type = "long")
    private Long sellerId;

    @Field
    @Indexed(name = "sellerName", type = "string")
    private String sellerName;  // Seller name (e.g., "EasyBuy")

    @Field
    @Indexed(type = "double")
    private double price;  // Price from the seller

    @Field
    @Indexed(type = "int")
    private int stock;  // Available stock from the seller

    @Field
    @Indexed(type = "double")
    private double sellerRating;  // Seller's rating (out of 5)

    @Field
    @Indexed(type = "int")
    private int productsSoldCount;  // Number of products sold by the seller

    @Field
    @Indexed(type = "int")
    private int totalProductsOffered;  // Total number of different products offered by the seller

    @Field
    @Indexed(type = "int")
    private int productReviews;  // Number of customer reviews for the product

    @Field
    @Indexed(name = "category", type = "string")
    private String category;  // Category of the product (e.g., "Smartphones")

    @Field
    @Indexed(type = "double")
    private double sellerRank;  // Rank of the seller

    public double getSellerRank() {
        return sellerRank;
    }

    public void setSellerRank(double sellerRank) {
        this.sellerRank = sellerRank;
    }

    public Long getProductOfferingId() {
        return productOfferingId;
    }

    public void setProductOfferingId(Long productOfferingId) {
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