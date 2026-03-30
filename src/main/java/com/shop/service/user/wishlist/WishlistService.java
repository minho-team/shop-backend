package com.shop.service.user.wishlist;

import java.util.List;

import com.shop.dto.user.wishlist.WishlistItemResponseDTO;

public interface WishlistService {
	boolean isWished(Long memberNo, Long productNo) throws Exception;

	void addWishlist(Long memberNo, Long productNo) throws Exception;

	void removeWishlist(Long memberNo, Long productNo) throws Exception;

	List<WishlistItemResponseDTO> getMyWishlist(Long memberNo) throws Exception;
}
