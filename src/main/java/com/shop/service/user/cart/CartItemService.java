package com.shop.service.user.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.dto.user.cart.CartItemAddRequest;
import com.shop.dto.user.cart.CartItemResponseDTO;

@Service
public interface CartItemService {

	public void addCartItem(Long memberNo, CartItemAddRequest request) throws Exception;

	public void deleteCartItem(Long cartItemNo) throws Exception;

	public void deleteAllCartItem(Long memberNo) throws Exception;

	public void updateCartItem(Long memberNo, Long cartItemNo, int cartQty) throws Exception;

	public List<CartItemResponseDTO> readCartItemByMemberNo(Long memberNo) throws Exception;
}
