package com.mohil_bansal.sellerservice.seller_service.repository;

import com.mohil_bansal.sellerservice.seller_service.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
}
