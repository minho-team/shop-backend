package com.shop.service.user.review;

import com.shop.dto.user.review.ReviewSaveRequestDTO;

public interface ReviewService {
    
    void registerReview(ReviewSaveRequestDTO dto);
}