package com.shop.mapper;

import com.shop.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductImageMapper {
	
	//초기 이미지 넣어놓는 용도
    void insertProductImg(ProductImage productImage);
}