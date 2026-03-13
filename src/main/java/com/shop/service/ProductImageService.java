package com.shop.service;

import com.shop.dto.ProductImageListDTO;

public interface ProductImageService {

	ProductImageListDTO getProductMainAndThumbImages(Long productNo);
}
