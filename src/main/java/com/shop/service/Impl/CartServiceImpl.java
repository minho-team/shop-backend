package com.shop.service.Impl;

import org.springframework.stereotype.Service;
import com.shop.mapper.CartMapper;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private final CartMapper cartMapper;

	@Override
	public void createCart() {
		cartMapper.createCart();
	}

	@Override
	public void updateCart(Long cartNo) {
		cartMapper.updateCart(cartNo);
	}

	@Override
	public void deleteCart(Long cartNo) {
		cartMapper.deleteCart(cartNo);
	}

	@Override
	public void readCart(Long cartNo) {
		cartMapper.readCart(cartNo);
	}

	@Override
	public void readAllCart() {
		cartMapper.readAllCart();
	}

	@Override
	public Member readOneMemberByCartNo(Long cartNo) throws Exception {
		cartMapper.readOneMemberByCartNo(cartNo);
	}

}
