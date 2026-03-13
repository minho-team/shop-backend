package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.dto.ProductImageDTO;
import com.shop.dto.ProductImageListDTO;
import com.shop.mapper.ProductImageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

	private final ProductImageMapper productImageMapper;

	@Override
	public ProductImageListDTO getProductMainAndThumbImages(Long productNo) {
		List<ProductImageDTO> images = productImageMapper.getProductImagesByTypes(productNo);

		images.forEach(img -> {
			if (img.getImageUrl() != null && !img.getImageUrl().startsWith("/upload/")) {
				img.setImageUrl("/upload/" + img.getImageUrl());
			}
		});

		return ProductImageListDTO.builder().productNo(productNo).images(images).build();
	}
}
