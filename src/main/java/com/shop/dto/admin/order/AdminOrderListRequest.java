package com.shop.dto.admin.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderListRequest {

	private int page = 1;
	private int size = 10;

	public int getOffset() {
		return (page - 1) * size;
	}
}