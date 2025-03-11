package com.mohil_bansal.search_service.repository;

import com.mohil_bansal.search_service.entity.ProductOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOfferingRepository extends SolrCrudRepository<ProductOffering, String> {

    @Query("productName:*?0*")
    List<ProductOffering> findByProductName(String productName);

    //Trying with pageable
//    @Query("productName:*?0* OR productName:?0*")
    @Query("productName:*?0*")
    Page<ProductOffering> findByProductName(String productName, Pageable pageable);


    @Query("productName:*?0* OR sellerName:*?0* OR category:*?0*")
    List<ProductOffering> findByProductNameContainingOrSellerNameContainingOrCategoryContaining(String productName, String sellerName, String categoryName);

}
