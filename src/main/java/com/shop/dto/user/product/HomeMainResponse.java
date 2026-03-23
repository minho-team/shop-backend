package com.shop.dto.user.product;

import java.util.List;

import lombok.Data;

@Data
public class HomeMainResponse {
    private List<HomeProductCardDto> newProducts;
    private List<HomeProductCardDto> bestProducts;
    private List<HomeProductCardDto> saleProducts;
    private List<HomeProductCardDto> recommendProducts;
    private List<HomeReviewDto> recentReviews;
    private List<PopularKeywordDto> popularKeywords;
}