package com.ishan_bhat.ReviewService.controller;



import com.ishan_bhat.ReviewService.dto.ReviewDto;
import com.ishan_bhat.ReviewService.entity.Review;
import com.ishan_bhat.ReviewService.service.ReviewService;
import com.ishan_bhat.ReviewService.utils.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;


    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{offeringId}")
    public ResponseEntity<CommonResponse<List<ReviewDto>>> getReviewsByOfferingId(@PathVariable Long offeringId) {
        System.out.println("yeah i came");
        List<ReviewDto> reviews = reviewService.getReviewsByOfferingId(offeringId);
        return ResponseEntity.ok(CommonResponse.success(reviews, HttpStatus.OK.value(), "Success"));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ReviewDto>> addReview(@Valid @RequestParam Long userId, @RequestParam Long productOfferingId, @RequestBody ReviewDto reviewDto) { // Expect ReviewDto in request body
        ReviewDto addedReview = reviewService.addReview(userId, productOfferingId, convertToEntity(reviewDto)); // Convert DTO to Entity before service call
        return new ResponseEntity<>(CommonResponse.success(convertToDto(addedReview), HttpStatus.CREATED.value(), "Review added successfully"), HttpStatus.CREATED); // Convert Entity back to DTO for response
    }

    @GetMapping("/{offeringId}/average-rating") // New endpoint for average rating
    public ResponseEntity<CommonResponse<Double>> getAverageRating(@PathVariable Long offeringId) {
        double averageRating = reviewService.getAverageRatingByOfferingId(offeringId);
        return ResponseEntity.ok(CommonResponse.success(averageRating, HttpStatus.OK.value(), "Average rating retrieved successfully"));
    }

    private ReviewDto convertToDto(ReviewDto review) {
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