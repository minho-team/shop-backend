package com.shop.service.user.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Product;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductUpdateRequest;
import com.shop.mapper.ProductMapper;

@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductMapper mapper;

	@Override
	public void insertProduct(ProductCreateRequest dto) throws Exception {
		mapper.insertProduct(dto);
		
	}


	@Override
	public void updateProducts(Long productNo, ProductUpdateRequest dto) {
		try {
			mapper.updateProducts(productNo, dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void deleteProduct(Long productNo) {
		try {
			mapper.deleteProduct(productNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public Product getOneProduct(Long productNo) throws Exception {
		return mapper.getOneProducts(productNo);

	}


	@Override
	public List<ProductListResponse> getAllProductToMainPage() throws Exception {
	    List<ProductListResponse> list = mapper.getAllProductToMainPage();

	    for (ProductListResponse dto : list) {
	        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
	            dto.setImageUrl("/upload/" + dto.getImageUrl());
	        }
	    }

	    return list;
	}


	@Override
	public List<Product> getAllProducts() throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAllProducts();
	}


	


}
