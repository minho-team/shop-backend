package com.shop.service.admin.category;

import java.util.List;

import com.shop.dto.admin.category.AdminCategoryListDTO;

public interface AdminCategoryService {
    List<AdminCategoryListDTO> getCategoryList() throws Exception;
}