package com.shop.mapper;

import java.util.List;

import com.shop.domain.CartItem;
import com.shop.dto.CartItemAddRequest;

public interface CartItemMapper {

	public void addCartItem(CartItem cartItem) throws Exception;

	public void deleteCartItem(Long cartItemNo) throws Exception;

	public void deleteAllCartItem() throws Exception;

	public void updateCartItem(Long cartItemNo) throws Exception;

	public List<CartItem> readAllCartItem() throws Exception;

}
