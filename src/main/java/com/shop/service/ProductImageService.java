package com.shop.service;

import com.shop.dto.user.product.ProductImageListDTO;

public interface ProductImageService {

	ProductImageListDTO getProductMainAndThumbImages(Long productNo);
}
