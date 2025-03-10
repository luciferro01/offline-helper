package com.ishan_bhat.ReviewService.service;



import com.ishan_bhat.ReviewService.dto.ReviewDto;
import com.ishan_bhat.ReviewService.entity.Review;
import com.ishan_bhat.ReviewService.exception.ResourceNotFoundException;
import com.ishan_bhat.ReviewService.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

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

    public ReviewDto addReview(Review review) {
        Review savedReview = reviewRepository.save(review);
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