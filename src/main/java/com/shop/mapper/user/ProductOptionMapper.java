package com.shop.mapper.user;

import java.util.List;
import java.util.Map;

import com.shop.domain.ProductOption;

public interface ProductOptionMapper {

	// 초기 이미지 넣는 용도
	void insertSeedOption(ProductOption productOption);

	List<ProductOption> getOptionsByProductNo(Long productNo) throws Exception;

	Map<String, Object> selectPaymentValidationItem(Long productOptionNo);
}
