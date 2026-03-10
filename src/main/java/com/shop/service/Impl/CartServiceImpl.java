package com.shop.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.Cart;
import com.shop.domain.Member;
import com.shop.mapper.CartMapper;
import com.shop.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private final CartMapper cartMapper;

	@Override
	public void createCart(Cart cart) throws Exception {
		cartMapper.createCart(cart);
	}

	@Override
	public void updateCart(Cart cart) throws Exception {
		cartMapper.updateCart(cart);
	}

	@Override
	public void deleteCart(Long cartNo) throws Exception {
		cartMapper.deleteCart(cartNo);
	}

	@Override
	public Cart readCart(Long cartNo) throws Exception {
		return cartMapper.readCart(cartNo);
	}

	@Override
	public List<Cart> readAllCart() throws Exception {
		return cartMapper.readAllCart();
	}

	@Override
	public Member readOneMemberByCartNo(Long cartNo) throws Exception {
		return cartMapper.readOneMemberByCartNo(cartNo);
	}

}
