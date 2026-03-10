package com.shop.mapper;

import java.util.List;
import com.shop.domain.Cart;
import com.shop.domain.Member;

public interface CartMapper {

    void createCart(Cart cart) throws Exception;    // Cart 파라미터 추가

    void updateCart(Cart cart) throws Exception;    // Long → Cart 변경

    void deleteCart(Long cartNo) throws Exception;

    Cart readCart(Long cartNo) throws Exception;

    List<Cart> readAllCart() throws Exception;

    Member readOneMemberByCartNo(Long cartNo) throws Exception;
}