package com.shop.domain;

import java.util.Date; 
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString           
@NoArgsConstructor  
@AllArgsConstructor
public class Review {
    private int reviewNo;
    private int memberNo;
    private int productNo;
    private int orderItemNo;
    private String title;
    private String content;
    private int rating;
        
    private Integer userHeight;   
    private Integer userWeight;  
    private String sizeRating;  
    
    private String imageUrl;
    private int likeCount;
    private Date createdAt; 
    private Date updatedAt;  
    private String deleteYn;
}