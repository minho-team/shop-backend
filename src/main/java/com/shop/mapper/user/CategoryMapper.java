package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.dto.user.category.HomeCategoryMenuDto;

@Mapper
public interface CategoryMapper {
    List<HomeCategoryMenuDto> selectHomeCategoryMenu() throws Exception;
}