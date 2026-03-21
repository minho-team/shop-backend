package com.shop.service.user.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.user.category.HomeCategoryMenuDto;
import com.shop.mapper.user.CategoryMapper;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<HomeCategoryMenuDto> getHomeCategoryMenu() throws Exception {
        return categoryMapper.selectHomeCategoryMenu();
    }
}