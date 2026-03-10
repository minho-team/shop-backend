package com.shop.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Cart {
    
    private Long cartNo;
    private Long memberNo;
    private LocalDateTime createdAt;
}