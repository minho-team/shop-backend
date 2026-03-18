package com.shop.service.user.review;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Review;

public interface ReviewService {
    
    void registerReview(Review review, MultipartFile file) throws Exception;

    List<Review> getReviewListByProduct(int productNo);
    
    boolean checkAlreadyReviewed(int orderItemNo);
}