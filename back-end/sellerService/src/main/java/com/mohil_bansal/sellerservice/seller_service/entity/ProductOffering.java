package com.mohil_bansal.sellerservice.seller_service.entity;

import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType; // Import JsonBinaryType

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@TypeDef(name = "jsonb-node", typeClass = JsonBinaryType.class) // Define jsonb-node type
public class ProductOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "seller_name", nullable = false)
    private String sellerName;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "sold")
    private Integer sold = 0;

    @Column(name = "rating")
    private Integer rating = null;

    @Column(name = "created_at")
    private Timestamp createdAt;

//    @Column(name = "images", columnDefinition = "jsonb")
//    @Type(type = "jsonb-node") // Use @Type, NO @Convert here in Seller_service as well
//    private JsonNode images;
    @Column(name = "product_image_url")
    private String productImageUrl;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}