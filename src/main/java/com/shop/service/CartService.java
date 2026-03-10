package com.shop.service;

import java.util.List;

import com.shop.domain.Cart;
import com.shop.domain.Member;

public interface CartService {

    void createCart(Cart cart) throws Exception;

    void updateCart(Cart cart) throws Exception;

    void deleteCart(Long cartNo) throws Exception;

    Cart readCart(Long cartNo) throws Exception;

    List<Cart> readAllCart() throws Exception;

    Member readOneMemberByCartNo(Long cartNo) throws Exception;
}