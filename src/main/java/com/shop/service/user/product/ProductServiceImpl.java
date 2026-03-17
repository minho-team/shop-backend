package com.shop.service.user.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Product;
import com.shop.domain.ProductOption;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductDetailResponse;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.dto.user.product.ProductUpdateRequest;
import com.shop.mapper.ProductMapper;
import com.shop.mapper.ProductOptionMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private ProductOptionMapper productOptionMapper;

	@Override
	public void insertProduct(ProductCreateRequest dto) throws Exception {
		productMapper.insertProduct(dto);

	}

	@Override
	public void updateProducts(Long productNo, ProductUpdateRequest dto) {
		try {
			productMapper.updateProducts(productNo, dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProduct(Long productNo) {
		try {
			productMapper.deleteProduct(productNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public ProductDetailResponse getOneProduct(Long productNo) throws Exception {
		Product product = productMapper.getOneProducts(productNo);
		List<ProductOption> options = productOptionMapper.getOptionsByProductNo(productNo);

		ProductDetailResponse response = new ProductDetailResponse();
		response.setProduct(product);
		response.setOptions(options);
		
		return response;
	}
	@Override
	public List<ProductListResponse> getAllProductToMainPage() throws Exception {
		List<ProductListResponse> list = productMapper.getAllProductToMainPage();

		for (ProductListResponse dto : list) {
			if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
				dto.setImageUrl("/upload/" + dto.getImageUrl());
			}
		}

		return list;
	}

	@Override
	public List<Product> getAllProducts() throws Exception {
		return productMapper.getAllProducts();
	}

	@Override
	public List<ProductListResponseDto> selectSearchProductList(Integer categoryId, String keyword) throws Exception {
		List<ProductListResponseDto> list = productMapper.selectSearchProductList(categoryId, keyword);
		for (ProductListResponseDto dto : list) {
			if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
				dto.setImageUrl("/upload/" + dto.getImageUrl());
			}
		}
		return list;
    }
}
