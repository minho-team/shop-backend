package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.CartItem;
import com.shop.dto.CartItemAddRequest;

@Service
public interface CartItemService {

	public void addCartItem(CartItemAddRequest request) throws Exception;

	public void deleteCartItem(Long cartItemNo) throws Exception;

	public void deleteAllCartItem() throws Exception;

	public void updateCartItem(Long cartItemNo) throws Exception;

	public List<CartItem> readAllCartItem() throws Exception;

}
