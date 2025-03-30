package com.ishan_bhat.ReviewService.service;



import com.ishan_bhat.ReviewService.dto.ProductOfferingDto;
import com.ishan_bhat.ReviewService.dto.ReviewDto;
import com.ishan_bhat.ReviewService.entity.Review;
import com.ishan_bhat.ReviewService.exception.ResourceNotFoundException;
import com.ishan_bhat.ReviewService.repository.ReviewRepository;
import com.ishan_bhat.ReviewService.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewDto> getReviewsByOfferingId(Long offeringId) {
        List<Review> reviews = reviewRepository.findByProductOfferingId(offeringId);
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found for offering ID: " + offeringId);
        }
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ReviewDto addReview(Long userId, Long productOfferingId, Review review) {
        review.setUserId(userId);
        review.setProductOfferingId(productOfferingId);
        Review savedReview = reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByProductOfferingId(productOfferingId);
        long count = reviews.size();
        double totalRating = reviews.stream().mapToInt(Review::getRating).sum();

        double newAvg = (totalRating + review.getRating()) / (count + 1);
        int rating = (int) Math.round(newAvg);

        ProductOfferingDto productOfferingDto = new ProductOfferingDto();
        productOfferingDto.setRating(rating);

        HttpEntity<ProductOfferingDto> request = new HttpEntity<>(productOfferingDto);
        ResponseEntity<CommonResponse> updateResponse = restTemplate.exchange(
                "http://localhost:8764/seller/offering/" + productOfferingId,
                HttpMethod.PUT,
                request,
                CommonResponse.class
        );
        return convertToDto(savedReview);
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return convertToDto(review);
    }

    public double getAverageRatingByOfferingId(Long offeringId) {
        List<Review> reviews = reviewRepository.findByProductOfferingId(offeringId);
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found for offering ID: " + offeringId + " to calculate average rating."); // <-- Throw ResourceNotFoundException
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }


    private ReviewDto convertToDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getUserId(),
                review.getReviewText(),
                review.getRating(),
                review.getProductOfferingId(),
                review.getCreatedAt()
        );
    }

    private Review convertToEntity(ReviewDto reviewDto) {
        Review review = new Review();
        review.setId(reviewDto.getId());
        review.setUserId(reviewDto.getUserId());
        review.setReviewText(reviewDto.getReviewText());
        review.setRating(reviewDto.getRating());
        review.setProductOfferingId(reviewDto.getProductOfferingId());
        review.setCreatedAt(reviewDto.getCreatedAt());
        return review;
    }
}