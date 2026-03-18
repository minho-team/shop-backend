package com.shop.dto.user.review;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDetailResponseDTO {
    private int reviewNo;
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
    private Date createdAt;
}