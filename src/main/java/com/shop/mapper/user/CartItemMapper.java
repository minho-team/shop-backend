package com.shop.mapper.user;

import java.util.List;

import com.shop.domain.CartItem;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.user.cart.CartItemResponseDTO;

public interface CartItemMapper {

	// 장바구니 아이템 추가
	void addCartItem(CartItem cartItem) throws Exception;

	// 장바구니 아이템 단건 삭제
	void deleteCartItem(Long cartItemNo) throws Exception;

	// 장바구니 아이템 전체 삭제
	void deleteAllCartItem(Long cartNo) throws Exception;

	// 장바구니 아이템 수량 수정
	void updateCartItem(CartItem cartItem) throws Exception;

	// 전체 장바구니 아이템 조회
	List<CartItem> readAllCartItem() throws Exception;

	// 회원 번호로 장바구니 아이템 조회
	List<CartItemResponseDTO> readCartItemByMemberNo(Long memberNo) throws Exception;

	// 장바구니에 같은 옵션 상품이 이미 있는지 조회
	CartItem findByCartNoAndProductOptionNo(CartItem cartItem) throws Exception;

	// 관리자 - 특정 회원 장바구니 담긴 상품 종류 수
	int countCartItemByMemberNo(Long memberNo) throws Exception;

	// ================================================
	// 관리자 - 특정 회원 장바구니 상품 목록 상세 조회
	// 상품명, 옵션, 수량, 가격 포함
	// ================================================
	List<AdminCartItemDTO> selectCartItemsWithProductByMemberNo(Long memberNo) throws Exception;
}