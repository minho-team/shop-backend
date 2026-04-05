package com.shop.mapper.user;

import java.util.List;
import java.util.Map;

import com.shop.domain.ProductOption;

public interface ProductOptionMapper {

	// 초기 이미지 넣는 용도
	void insertSeedOption(ProductOption productOption);

	List<ProductOption> getOptionsByProductNo(Long productNo) throws Exception;

	Map<String, Object> selectPaymentValidationItem(Long productOptionNo);

	//환불 중 재고를 환불수량에 맞게 + 시키는 함수
	void updateQuantityWhileRefunding(Long productOptionNo, Long refundQuantity);
}
