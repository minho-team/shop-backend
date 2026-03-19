package com.shop.service.user.cart;

import java.util.List;

import com.shop.domain.Cart;
import com.shop.domain.Member;
import com.shop.dto.user.cart.CartItemAddRequest;

public interface CartService {

    void createCart(Long memberNo) throws Exception;

    void updateCart(Cart cart) throws Exception;

    void deleteCart(Long cartNo) throws Exception;

    Cart readCart(Long cartNo) throws Exception;
    
    Cart readCartByMemberNo(Long memberNo) throws Exception;
    
    List<Cart> readAllCart() throws Exception;

    Member readOneMemberByCartNo(Long cartNo) throws Exception;
}