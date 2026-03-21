package com.shop.service.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.dto.admin.category.AdminCategoryListDTO;
import com.shop.mapper.admin.AdminCategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final AdminCategoryMapper adminCategoryMapper;

    @Override
    public List<AdminCategoryListDTO> getCategoryList() throws Exception {
        return adminCategoryMapper.getCategoryList();
    }
}