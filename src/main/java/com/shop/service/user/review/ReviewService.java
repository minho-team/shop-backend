package com.shop.service.user.review;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Review;
import com.shop.dto.user.review.MyReviewResponseDTO;
import com.shop.dto.user.review.ReviewDTO;

public interface ReviewService {
    
    void registerReview(Review review, MultipartFile file) throws Exception;

    List<Review> getReviewListByProduct(Long productNo);
    
    boolean checkAlreadyReviewed(Long orderItemNo);
    
    List<MyReviewResponseDTO> getMyReviews(Long memberNo);
    
    void removeReview(Long reviewNo, Long memberNo);
    
    void deleteReview(Long reviewNo, Long memberNo);
    
    public void updateReview(ReviewDTO dto, MultipartFile uploadFile);
}