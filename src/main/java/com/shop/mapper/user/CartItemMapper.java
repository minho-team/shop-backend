package com.shop.mapper.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.CartItem;
import com.shop.dto.user.cart.CartItemResponseDTO;

// 사용자 장바구니 DB 접근 Mapper
// 관리자용 장바구니 조회는 AdminCartItemMapper로 분리
@Mapper
public interface CartItemMapper {

    // 장바구니 아이템 추가
    void addCartItem(CartItem cartItem) throws Exception;

    // 장바구니 아이템 단건 삭제
    void deleteCartItem(Long cartItemNo) throws Exception;

    // 장바구니 아이템 전체 삭제
    void deleteAllCartItem(Long cartNo) throws Exception;

    // 장바구니 아이템 수량 수정
    void updateCartItem(CartItem cartItem) throws Exception;

    // 회원의 장바구니에서 특정 cartItem 삭제
    void deleteCartItemByMemberNoAndCartItemNo(
            @Param("memberNo") Long memberNo,
            @Param("cartItemNo") Long cartItemNo) throws Exception;

    // 전체 장바구니 아이템 조회
    List<CartItem> readAllCartItem() throws Exception;

    // 회원 번호로 장바구니 아이템 조회
    List<CartItemResponseDTO> readCartItemByMemberNo(Long memberNo) throws Exception;

    // 장바구니에 같은 옵션 상품이 이미 있는지 조회 (중복 담기 방지)
    CartItem findByCartNoAndProductOptionNo(CartItem cartItem) throws Exception;
}