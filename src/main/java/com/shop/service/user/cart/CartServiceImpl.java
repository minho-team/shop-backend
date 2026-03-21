package com.shop.service.user.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.Cart;
import com.shop.domain.Member;
import com.shop.mapper.user.CartMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartMapper mapper;

	@Override
	public void createCart(Long memberNo) throws Exception {
		// 1. 먼저 장바구니가 있는지 확인
		Cart cart = mapper.readCartByMemberNo(memberNo);

		// 2. 장바구니가 없으면 생성하고 다시 읽어오기
		if (cart == null) {
			Cart newCart = new Cart();
			newCart.setMemberNo(memberNo);

			mapper.createCart(newCart);
		}
	}

	@Override
	public void updateCart(Cart cart) throws Exception {
		mapper.updateCart(cart);
	}

	@Override
	public void deleteCart(Long cartNo) throws Exception {
		mapper.deleteCart(cartNo);
	}

	@Override
	public Cart readCart(Long cartNo) throws Exception {
		return mapper.readCart(cartNo);
	}

	@Override
	public Cart readCartByMemberNo(Long memberNo) throws Exception {
		return mapper.readCartByMemberNo(memberNo);
	}

	@Override
	public List<Cart> readAllCart() throws Exception {
		return mapper.readAllCart();
	}

	@Override
	public Member readOneMemberByCartNo(Long cartNo) throws Exception {
		return mapper.readOneMemberByCartNo(cartNo);
	}

}
