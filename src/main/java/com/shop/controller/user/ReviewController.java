package com.shop.controller.user;

import com.shop.dto.user.review.ReviewSaveRequestDTO;
import com.shop.service.user.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/save")
    public ResponseEntity<String> saveReview(ReviewSaveRequestDTO requestDTO) {
        reviewService.registerReview(requestDTO);
        return ResponseEntity.ok("Success");
    }
}