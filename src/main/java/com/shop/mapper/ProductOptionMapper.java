package com.shop.mapper;

import java.util.List;

import com.shop.domain.ProductOption;

public interface ProductOptionMapper {

	// 초기 이미지 넣는 용도
	void insertSeedOption(ProductOption productOption);

	List<ProductOption> getOptionsByProductNo(Long productNo) throws Exception;
}
