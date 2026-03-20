package com.shop.dto.user.review;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class ReviewSaveRequestDTO {
    private Long memberNo;       
    private Long productNo;      
    private Long orderItemNo;   
    private String title;
    private String content;
    private int rating;
    
    private Integer userHeight;
    private Integer userWeight;
    private String sizeRating;
    
    // 파일 업로드 처리용
    private MultipartFile uploadFile; 
    private String imageUrl;
}