package com.shop.service.user.product;

import java.util.List;

import com.shop.domain.Product;
import com.shop.dto.user.product.HomeMainResponse;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductDetailResponse;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.dto.user.product.ProductUpdateRequest;

public interface ProductService {

	void insertProduct(ProductCreateRequest dto) throws Exception;

	List<Product> getAllProducts() throws Exception;

	// 상품의 정보와 상품옵션을 가져오는 함수
	ProductDetailResponse getOneProduct(Long productNo) throws Exception;

	void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception;

	void deleteProduct(Long productNo) throws Exception;

	List<ProductListResponse> getAllProductToMainPage() throws Exception;

	List<ProductListResponseDto> selectSearchProductList(Integer categoryId, String keyword, String sort,
			Boolean discountOnly) throws Exception;

	HomeMainResponse getHomeMainData() throws Exception;

	// 현재 상품과 같은 카테고리의 관련상품 목록 조회
	List<ProductListResponse> getRelatedProducts(Long productNo) throws Exception;
}