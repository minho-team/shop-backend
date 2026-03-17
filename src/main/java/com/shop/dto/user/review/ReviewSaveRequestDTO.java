package com.shop.dto.user.review;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class ReviewSaveRequestDTO {
    private int memberNo;
    private int productNo;
    private int orderItemNo;
    private String title;
    private String content;
    private int rating;
    private MultipartFile uploadFile;
}