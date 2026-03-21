package com.shop.service.user.refund;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.dto.user.refund.RefundCreateItemRequestDTO;
import com.shop.dto.user.refund.RefundCreateRequestDTO;
import com.shop.mapper.user.MemberMapper;
import com.shop.mapper.user.RefundMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
	private final RefundMapper refundMapper;
	private final MemberMapper memberMapper;

	@Override
	@Transactional
	public void createRefund(String memberId, RefundCreateRequestDTO requestDTO) throws Exception{
		Member member = memberMapper.readOneMember(memberId);
		if (member == null) {
			throw new RuntimeException("회원이 없습니다.");
		}

		Long memberNo = member.getMemberNo();

		refundMapper.insertRefund(requestDTO.getOrderNo(), memberNo, requestDTO.getRefundReason());

		Long refundNo = refundMapper.getCurrentRefundNo();

		List<RefundCreateItemRequestDTO> items = requestDTO.getItems();
		if (items == null || items.isEmpty()) {
			throw new RuntimeException("환불 상품이 없습니다.");
		}

		long totalRefundAmount = 0L;

		//환불작성서에 있는 아이템을 하나씩 돌려가면서 그에 대한 조작하기 
		for (RefundCreateItemRequestDTO item : items) {
			refundMapper.insertRefundItem(refundNo, item.getOrderItemNo(), item.getRefundQuantity(),
					item.getRefundAmount());

			refundMapper.increaseRefundedQuantity(item.getOrderItemNo(), item.getRefundQuantity());

			refundMapper.updateOrderItemStatus(item.getOrderItemNo(), "REFUND_REQUESTED");
			//환불의 총액 계산
			totalRefundAmount += item.getRefundAmount();
		}

		//환불 테이블에 총액 갱신
		refundMapper.updateRefundTotalAmount(refundNo, totalRefundAmount);

		Long orderNo = requestDTO.getOrderNo();
		List<String> orderItemStatuses = refundMapper.findOrderItemStatusesByOrderNo(orderNo);
		String orderStatus = calculateOrderStatus(orderItemStatuses);
		//환불 상태 처리 후에 orders의 테이블에도 상태를 또 계산해야함
		refundMapper.updateOrderStatus(orderNo, orderStatus);
	}

	private String calculateOrderStatus(List<String> statuses) {
		if (statuses == null || statuses.isEmpty()) {
			throw new RuntimeException("주문상품 상태가 없습니다.");
		}

		if (statuses.stream().allMatch(status -> "PENDING_PAYMENT".equals(status))) {
			return "PENDING_PAYMENT";
		}

		if (statuses.stream().allMatch(status -> "CANCELED".equals(status))) {
			return "CANCELED";
		}

		if (statuses.stream().allMatch(status -> "DELIVERED".equals(status))) {
			return "DELIVERED";
		}

		if (statuses.stream().anyMatch(status -> "SHIPPING".equals(status))) {
			return "SHIPPING";
		}

		if (statuses.stream().anyMatch(status -> "PREPARING".equals(status))) {
			return "PREPARING";
		}

		return "PAYMENT_COMPLETED";
	}
}
