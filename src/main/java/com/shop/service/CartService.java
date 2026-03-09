package com.shop.service;

public interface CartService {

	void createCart() throws Exception;

	void updateCart(Long cartNo)throws Exception;

	void deleteCart(Long cartNo) throws Exception;

	void readCart(Long cartNo) throws Exception;

	void readAllCart() throws Exception;
	
	Member readOneMemberByCartNo(Long cartNo) throws Exception;

}
