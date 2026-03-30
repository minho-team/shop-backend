package com.shop.service.user.wishlist;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Wishlist;
import com.shop.dto.user.wishlist.WishlistItemResponseDTO;
import com.shop.mapper.user.WishlistMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

	private final WishlistMapper wishlistMapper;

	@Override
	@Transactional(readOnly = true)
	public boolean isWished(Long memberNo, Long productNo) throws Exception {
		if (memberNo == null || productNo == null) {
			return false;
		}
		return wishlistMapper.countByMemberNoAndProductNo(memberNo, productNo) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addWishlist(Long memberNo, Long productNo) throws Exception {
		if (memberNo == null || productNo == null) {
			return;
		}

		if (wishlistMapper.countByMemberNoAndProductNo(memberNo, productNo) > 0) {
			return;
		}

		Wishlist wishlist = new Wishlist();
		wishlist.setMemberNo(memberNo);
		wishlist.setProductNo(productNo);
		wishlistMapper.insertWishlist(wishlist);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeWishlist(Long memberNo, Long productNo) throws Exception {
		if (memberNo == null || productNo == null) {
			return;
		}
		wishlistMapper.deleteWishlist(memberNo, productNo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WishlistItemResponseDTO> getMyWishlist(Long memberNo) throws Exception {
		List<WishlistItemResponseDTO> list = wishlistMapper.selectMyWishlist(memberNo);

		if (list != null) {
			for (WishlistItemResponseDTO dto : list) {
				dto.setSalePrice(calculateSalePrice(dto.getPrice(), dto.getDiscountRate()));

				if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
					dto.setImageUrl("/upload/" + dto.getImageUrl());
				}
			}
		}

		return list;
	}

	private Long calculateSalePrice(Long price, Integer discountRate) {
		if (price == null) {
			return 0L;
		}

		if (discountRate == null || discountRate <= 0) {
			return price;
		}

		return (long) (Math.floor((price * (100 - discountRate)) / 100.0 / 100.0) * 100);
	}
}
