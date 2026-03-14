package com.shop.service.user.product;

import com.shop.dto.user.product.ProductImageListDTO;

public interface ProductImageService {

	ProductImageListDTO getProductMainAndThumbImages(Long productNo);
}
