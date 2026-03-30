package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shop.domain.Wishlist;
import com.shop.dto.user.wishlist.WishlistItemResponseDTO;

public interface WishlistMapper {
	// 해당 회원이 해당 상품을 이미 찜했는지 확인
	int countByMemberNoAndProductNo(@Param("memberNo") Long memberNo, @Param("productNo") Long productNo);

	// 찜 추가
	void insertWishlist(Wishlist wishlist);

	// 찜 삭제
	void deleteWishlist(@Param("memberNo") Long memberNo, @Param("productNo") Long productNo);

	// 내 찜 목록 조회
	List<WishlistItemResponseDTO> selectMyWishlist(@Param("memberNo") Long memberNo);
}
