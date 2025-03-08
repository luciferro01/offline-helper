package com.mohil_bansal.sellerservice.seller_service.repository;

import com.mohil_bansal.sellerservice.seller_service.entity.ProductOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOfferingRepository extends JpaRepository<ProductOffering, Long> {
    List<ProductOffering> findBySellerId(Long sellerId);
    boolean existsBySellerIdAndProductId(Long sellerId, Long productId);
}
