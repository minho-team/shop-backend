package com.shop.mapper;

public interface CartMapper {

	void createCart() throws Exception;

	void updateCart(Long cartNo) throws Exception;

	void deleteCart(Long cartNo) throws Exception;

	Cart readCart(Long cartNo) throws Exception;

	List<Cart> readAllCart() throws Exception;

	Cart readOneMemberByCartNo(Long cartNo) throws Exception;

}
