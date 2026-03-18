package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.dto.admin.category.AdminCategoryListDTO;

@Mapper
public interface AdminCategoryMapper {
    List<AdminCategoryListDTO> getCategoryList();
}