package com.shop.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Product;
import com.shop.dto.ProductCreateRequest;
import com.shop.dto.ProductUpdateRequest;
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
	public List<Product> getAllProducts() throws Exception {
		return mapper.getAllProducts();
		
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


	


}
