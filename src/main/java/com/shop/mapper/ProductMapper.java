package com.shop.mapper;

import java.util.List;

import com.shop.domain.Product;
import com.shop.dto.ProductCreateRequest;
import com.shop.dto.ProductUpdateRequest;

public interface ProductMapper {

	public void insertProduct(ProductCreateRequest dto) throws Exception;
	
	//초기 이미지 넣는 용도
	public void insertSeedProduct(Product product) throws Exception;

	public List<Product> getAllProducts() throws Exception;
	
	public Product getOneProducts(Long productNo) throws Exception;

	public void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception;

	public void deleteProduct (Long productNo)throws Exception;
}
