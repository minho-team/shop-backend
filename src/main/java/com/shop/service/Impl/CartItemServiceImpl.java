package com.shop.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.shop.domain.CartItem;
import com.shop.dto.CartItemAddRequest;
import com.shop.mapper.CartItemMapper;
import com.shop.service.CartItemService;

public class CartItemServiceImpl implements CartItemService {
	@Autowired
	private CartItemMapper mapper;
	
	@Override
	public void addCartItem(CartItemAddRequest request) throws Exception {
		mapper.addCartItem(request);
	}

	@Override
	public void deleteCartItem(Long cartItemNo) throws Exception {
		mapper.deleteCartItem(cartItemNo);
	}

	@Override
	public void deleteAllCartItem() throws Exception {
		mapper.deleteAllCartItem();
	}

	@Override
	public void updateCartItem(Long cartItemNo) throws Exception {
		mapper.updateCartItem(cartItemNo);
	}

	@Override
	public List<CartItem> readAllCartItem() throws Exception {
		return mapper.readAllCartItem();
	}

}
