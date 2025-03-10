package com.ishan_bhat.ReviewService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long userId;
    private String reviewText;
    @Min(1)
    @Max(5)
    private Integer rating;
    private Long productOfferingId;
    private LocalDateTime createdAt;
}