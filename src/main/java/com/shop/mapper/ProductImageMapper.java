package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.ProductImage;
import com.shop.dto.user.product.ProductImageDTO;

@Mapper
public interface ProductImageMapper {

	// 초기 이미지 넣어놓는 용도
	void insertSeedProductImg(ProductImage productImage);

	List<ProductImageDTO> getProductMainAndThumbImages(@Param("productNo") Long productNo);
}