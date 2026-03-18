package com.shop.dto.admin.category;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminCategoryListDTO {
    private Long categoryId;
    private String name;
    private Long parentId;
    private LocalDateTime createdAt;
    private String useYn;
    private Integer depth;
}