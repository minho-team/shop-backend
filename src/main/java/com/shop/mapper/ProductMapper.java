package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shop.domain.Product;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.dto.user.product.ProductUpdateRequest;

public interface ProductMapper {

	public void insertProduct(ProductCreateRequest dto) throws Exception;

	// 초기 이미지 넣는 용도
	public void insertSeedProduct(Product product) throws Exception;

	public List<Product> getAllProducts() throws Exception;

	public Product getOneProducts(Long productNo) throws Exception;

	public void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception;

	public void deleteProduct(Long productNo) throws Exception;

	// 메인 페이지에서 섬네일과 함께 product 내려주는 매퍼
	public List<ProductListResponse> getAllProductToMainPage() throws Exception;

	public List<ProductListResponseDto> selectProductList(@Param("categoryId") Long categoryId) throws Exception;
}
