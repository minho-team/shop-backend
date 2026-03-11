package com.shop.service;

import java.util.List;

import com.shop.domain.Product;
import com.shop.dto.ProductCreateRequest;
import com.shop.dto.ProductUpdateRequest;

public interface ProductService {

	public void insertProduct(ProductCreateRequest dto) throws Exception;

	public List<Product> getAllProducts() throws Exception;
	
	public Product getOneProduct(Long productNo) throws Exception;

	public void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception;

	public void deleteProduct(Long productNo) throws Exception;

}
