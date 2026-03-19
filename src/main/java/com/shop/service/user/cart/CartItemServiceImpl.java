package com.shop.service.user.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.Cart;
import com.shop.domain.CartItem;
import com.shop.dto.user.cart.CartItemAddRequest;
import com.shop.dto.user.cart.CartItemResponseDTO;
import com.shop.mapper.CartItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

	private final CartItemMapper cartItemMapper;
	private final CartService cartService;

	@Override
	public void addCartItem(Long memberNo, CartItemAddRequest request) throws Exception {
		Cart cart = cartService.readCartByMemberNo(memberNo);

		if (cart == null) {
			cartService.createCart(memberNo);
			cart = cartService.readCartByMemberNo(memberNo);
		}

		CartItem findItem = new CartItem();
		findItem.setCartNo(cart.getCartNo());
		findItem.setProductOptionNo(request.getProductOptionNo());

		CartItem existedCartItem = cartItemMapper.findByCartNoAndProductOptionNo(findItem);

		if (existedCartItem != null) {
			int newQuantity = existedCartItem.getQuantity() + request.getQuantity();

			CartItem updateItem = new CartItem();
			updateItem.setCartItemNo(existedCartItem.getCartItemNo());
			updateItem.setQuantity(newQuantity);

			cartItemMapper.updateCartItem(updateItem);
		} else {
			CartItem cartItem = new CartItem();
			cartItem.setCartNo(cart.getCartNo());
			cartItem.setProductOptionNo(request.getProductOptionNo());
			cartItem.setQuantity(request.getQuantity());

			cartItemMapper.addCartItem(cartItem);
		}
	}

	@Override
	public void deleteCartItem(Long cartItemNo) throws Exception {
		cartItemMapper.deleteCartItem(cartItemNo);
	}

	@Override
	public void deleteAllCartItem(Long memberNo) throws Exception {
		Cart cart = cartService.readCartByMemberNo(memberNo);

		if (cart != null) {
			cartItemMapper.deleteAllCartItem(cart.getCartNo());
		}
	}

	@Override
	public void updateCartItem(Long memberNo, Long cartItemNo, int cartQty) throws Exception {
		Cart cart = cartService.readCartByMemberNo(memberNo);

		if (cart == null) {
			throw new Exception("장바구니가 존재하지 않습니다.");
		}

		CartItem cartItem = new CartItem();
		cartItem.setCartItemNo(cartItemNo);
		cartItem.setCartNo(cart.getCartNo());
		cartItem.setQuantity(cartQty);

		cartItemMapper.updateCartItem(cartItem);
	}

	@Override
	public List<CartItemResponseDTO> readCartItemByMemberNo(Long memberNo) throws Exception {
		return cartItemMapper.readCartItemByMemberNo(memberNo);
	}

}
