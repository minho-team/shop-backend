package com.shop.dto.user.order;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponseDTO {
	private Long orderNo;
	private Date createdAt;
	private Long totalPrice;
}