package com.shop.dto.user.product;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeReviewDto {
    private Long reviewNo;
    private String memberNickName;
    private String productName;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}