package com.mohil_bansal.sellerservice.seller_service.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String phoneNumber;
    private String address;
    private Integer totalStock;
    private Integer totalSold;
    private Double rating;
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

}
