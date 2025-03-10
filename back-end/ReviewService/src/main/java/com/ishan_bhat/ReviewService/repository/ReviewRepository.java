package com.ishan_bhat.ReviewService.repository;


import com.ishan_bhat.ReviewService.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductOfferingId(Long productOfferingId); // Method to find reviews by offering ID
}