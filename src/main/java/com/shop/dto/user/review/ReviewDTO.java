package com.shop.dto.user.review;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewDTO {
	private Long reviewNo;        
    private Long productNo;      
    private Long memberNo;      
    private String title;
    private String content;      
    private int rating;          
    private LocalDateTime createdAt; 
    private String itemName;    
    private String memberName;   
    private String imageUrl;

    private Integer userHeight; 
    private Integer userWeight; 
    private String sizeRating;
}