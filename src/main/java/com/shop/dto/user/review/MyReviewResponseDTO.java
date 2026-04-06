package com.shop.dto.user.review;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyReviewResponseDTO {
	private Long reviewNo;
    private Long productNo;
    private String itemName;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private Long orderNo;
    private String imageUrl;
}
