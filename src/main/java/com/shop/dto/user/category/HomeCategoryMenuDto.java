package com.shop.dto.user.category;

import lombok.Data;

@Data
public class HomeCategoryMenuDto {
    private Long rootCategoryId;
    private String rootCategoryName;
    private Long sectionCategoryId;
    private String sectionCategoryName;
    private Long categoryId;
    private String categoryName;
}