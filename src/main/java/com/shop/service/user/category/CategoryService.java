package com.shop.service.user.category;

import java.util.List;

import com.shop.dto.user.category.HomeCategoryMenuDto;

public interface CategoryService {
    List<HomeCategoryMenuDto> getHomeCategoryMenu() throws Exception;
}