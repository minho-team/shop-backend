package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shop.domain.Product;
import com.shop.dto.user.product.HomeProductCardDto;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.dto.user.product.ProductUpdateRequest;

public interface ProductMapper {

	void insertProduct(ProductCreateRequest dto) throws Exception;

	void insertSeedProduct(Product product) throws Exception;

	List<Product> getAllProducts() throws Exception;

	Product getOneProducts(Long productNo) throws Exception;

	void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception;

	void deleteProduct(Long productNo) throws Exception;

	List<ProductListResponse> getAllProductToMainPage() throws Exception;

	List<ProductListResponseDto> selectSearchProductList(@Param("categoryId") Integer categoryId,
			@Param("keyword") String keyword, @Param("sort") String sort, @Param("discountOnly") Boolean discountOnly)
			throws Exception;

	List<HomeProductCardDto> selectHomeNewProducts() throws Exception;

	List<HomeProductCardDto> selectHomeBestProducts() throws Exception;

	List<HomeProductCardDto> selectHomeSaleProducts() throws Exception;

	List<HomeProductCardDto> selectHomeRecommendProducts() throws Exception;

	List<com.shop.dto.user.product.PopularKeywordDto> selectPopularKeywords() throws Exception;

	void incrementViewCount(@org.apache.ibatis.annotations.Param("productNo") Long productNo) throws Exception;

	// 현재 상품의 categoryId와 productNo를 받아 같은 카테고리이면서 현재 상품은 제외한 관련상품 목록 조회
	List<ProductListResponse> selectRelatedProducts(@Param("categoryId") Long categoryId,
			@Param("productNo") Long productNo) throws Exception;
}