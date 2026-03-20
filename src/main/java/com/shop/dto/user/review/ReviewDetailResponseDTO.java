package com.shop.dto.user.review;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDetailResponseDTO {
    private Long reviewNo;        
    private String memberName;   
    private String productName;  
    private String title;
    private String content;
    private int rating;
    
    private Integer userHeight; 
    private Integer userWeight; 
    private String sizeRating;  
    
    private String imageUrl;     
    private int likeCount;
    private LocalDateTime createdAt;
}