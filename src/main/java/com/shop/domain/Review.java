package com.shop.domain;

import java.time.LocalDateTime; 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long reviewNo;
    private Long memberNo;
    private Long productNo;
    private Long orderItemNo;
    
    private String title;
    private String content;
    private int rating;
    
    private Integer userHeight;   
    private Integer userWeight;  
    private String sizeRating;  
    
    private String imageUrl;
    private int likeCount;
    
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt;  
    
    private String deleteYn; 
}