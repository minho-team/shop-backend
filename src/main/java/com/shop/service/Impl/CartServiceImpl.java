package com.shop.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.Cart;
import com.shop.domain.CartItem;
import com.shop.domain.Member;
import com.shop.dto.user.cart.CartItemAddRequest;
import com.shop.mapper.CartItemMapper;
import com.shop.mapper.CartMapper;
import com.shop.mapper.ProductMapper;
import com.shop.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private final CartMapper cartMapper;
	private final CartItemMapper cartItemMapper;
	private final ProductMapper productMapper;

	@Override
	public void createCart(Long memberNo, CartItemAddRequest dto) throws Exception {
		// 1. 먼저 장바구니가 있는지 확인
		Cart cart = cartMapper.readCart(memberNo);

		// 2. 장바구니가 없으면 생성하고 다시 읽어오기
		if (cart == null) {
			cartMapper.createCart(memberNo);
			cart = cartMapper.readCart(memberNo);
		}

		CartItem cartItem = new CartItem();

		cartItem.setCartNo(cart.getCartNo());
		cartItem.setProductOptionNo(dto.getProductOptionNo());
		cartItem.setQuantity(dto.getQuantity());

		// 3. 최종적으로 장바구니 아이템 추가
		cartItemMapper.addCartItem(cartItem);

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
	public Cart readCartByMemberNo(Long memberNo) throws Exception {
		return cartMapper.readCartByMemberNo(memberNo);
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
