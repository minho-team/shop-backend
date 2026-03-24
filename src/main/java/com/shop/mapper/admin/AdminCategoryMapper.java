package com.shop.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.shop.dto.admin.category.AdminCategoryListDTO;

// 관리자 카테고리 관리 DB 접근 Mapper
@Mapper
public interface AdminCategoryMapper {

    // 전체 카테고리 목록 조회 (계층 구조 포함)
    List<AdminCategoryListDTO> getCategoryList();
}