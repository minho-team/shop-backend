package com.shop.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Review;
import com.shop.dto.user.review.ReviewSaveRequestDTO;
import com.shop.service.user.review.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/register")
    public ResponseEntity<?> registerReview(
            @ModelAttribute ReviewSaveRequestDTO dto, 
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile
    ) {
        log.info("리뷰 등록 시도 - 상품번호: {}, 주문항목번호: {}, 회원번호: {}", 
                 dto.getProductNo(), dto.getOrderItemNo(), dto.getMemberNo());

        try {
            Review review = new Review();
            review.setMemberNo(dto.getMemberNo()); 
            review.setProductNo(dto.getProductNo());
            review.setOrderItemNo(dto.getOrderItemNo());
            review.setTitle(dto.getTitle());
            review.setContent(dto.getContent());
            review.setRating(dto.getRating());
            review.setUserHeight(dto.getUserHeight());
            review.setUserWeight(dto.getUserWeight());
            review.setSizeRating(dto.getSizeRating());

            reviewService.registerReview(review, uploadFile);

            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            log.error("리뷰 등록 중 예외 발생", e);
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productNo}")
    public ResponseEntity<?> getProductReviews(@PathVariable int productNo) {
        return ResponseEntity.ok(reviewService.getReviewListByProduct(productNo));
    }

    @GetMapping("/check/{orderItemNo}")
    public ResponseEntity<Boolean> checkReviewed(@PathVariable int orderItemNo) {
        boolean isReviewed = reviewService.checkAlreadyReviewed(orderItemNo);
        return ResponseEntity.ok(isReviewed);
    }
}