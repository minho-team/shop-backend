package com.shop.service.user.refund;

import com.shop.dto.user.refund.RefundCreateRequestDTO;

public interface RefundService {
	void createRefund(String memberId, RefundCreateRequestDTO requestDTO) throws Exception;
}
