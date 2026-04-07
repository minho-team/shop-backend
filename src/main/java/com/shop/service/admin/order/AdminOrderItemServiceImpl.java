package com.shop.service.admin.order;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.dto.admin.order.AdminOrderItemDetailResponseDTO;
import com.shop.mapper.admin.AdminOrderItemMapper;
import com.shop.mapper.user.MemberMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.service.user.member.MemberService;
import com.shop.service.user.roulette.RouletteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminOrderItemServiceImpl implements AdminOrderItemService {

	private final AdminOrderItemMapper adminOrderItemMapper;
	private final MemberService memberService; 
	private final MemberMapper memberMapper; 
	private final OrdersMapper ordersMapper;
	private final RouletteService rouletteService;
	
	  private static final Map<String, Long> GRADE_COUPON_MAP = Map.of(
		        "SILVER", 18L,
		        "GOLD",   19L,
		        "VIP",    20L,
		        "VVIP",   21L
		    );
		    private static final List<String> GRADE_ORDER = List.of("BASIC", "SILVER", "GOLD", "VIP", "VVIP");

	@Override
	public List<AdminOrderItemDetailResponseDTO> getOrderItemList(Long orderNo) throws Exception {
		List<AdminOrderItemDetailResponseDTO> list = adminOrderItemMapper.getAdminOrderItemList(orderNo);
		if (list != null) {
			for (AdminOrderItemDetailResponseDTO item : list) {
				if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
					item.setImageUrl("/upload/" + item.getImageUrl());
				}
			}
		}

		return list;
	}
	
	// 주문상품 개별 상태 변경
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderItemStatus(Long orderItemNo, String orderItemStatus) throws Exception {
	    log.info("주문상품 상태 변경 시작 - orderItemNo: {}, newStatus: {}", orderItemNo, orderItemStatus);

	    // 1. 현재 상태 조회 삭제 (변수 미사용으로 인한 경고 해결)
	    
	    // 2. 상태 업데이트
	    int updatedCount = adminOrderItemMapper.updateOrderItemStatus(orderItemNo, orderItemStatus);
	    if (updatedCount == 0) throw new RuntimeException("해당 주문상품이 없습니다.");

	    // 3. 등급 시스템 연동 (금액 기반이므로 중복 체크 없이 재계산 호출)
	    if ("DELIVERED".equals(orderItemStatus) || "REFUNDED".equals(orderItemStatus) || "CANCELED".equals(orderItemStatus)) {
	        Long memberNo = adminOrderItemMapper.getMemberNoByOrderItemNo(orderItemNo);
	        if (memberNo != null) {
	            updateGradeAfterStatusChange(memberNo);
	        }
	    }
	    
	    syncTotalOrderStatus(orderItemNo);
	}
	
	private void updateGradeAfterStatusChange(Long memberNo) throws Exception {
        long totalAmount = ordersMapper.selectTotalPurchaseAmount(memberNo);

        String newGrade = "BASIC";
        if (totalAmount >= 1000000)     newGrade = "VVIP";
        else if (totalAmount >= 500000) newGrade = "VIP";
        else if (totalAmount >= 300000) newGrade = "GOLD";
        else if (totalAmount >= 100000) newGrade = "SILVER";

        Member member = memberMapper.readOneMemberByNo(memberNo);
        String oldGrade = (member != null && member.getGrade() != null)
                          ? member.getGrade() : "BASIC";

        memberService.updateMemberGradeDirectly(memberNo, newGrade);
        log.info("[관리자 로직] 회원 {} 등급 갱신: {} → {} (누적금액: {}원)", memberNo, oldGrade, newGrade, totalAmount);

        if (isGradeUpgraded(oldGrade, newGrade)) {
            Long couponNo = GRADE_COUPON_MAP.get(newGrade);
            if (couponNo != null) {
                if (!rouletteService.isGradeCouponAlreadyIssued(memberNo, couponNo)) {
                    rouletteService.issueGradeCoupon(memberNo, couponNo);
                    log.info("[등급 쿠폰 지급] 회원: {}, {} 달성 → 쿠폰 {}번 지급 완료", memberNo, newGrade, couponNo);
                } else {
                    log.info("[등급 쿠폰 중복 방지] 회원: {}, {} 쿠폰 이미 보유", memberNo, newGrade);
                }
            }
        }
    }

    // ↓ 이 메서드도 새로 추가
    private boolean isGradeUpgraded(String oldGrade, String newGrade) {
        return GRADE_ORDER.indexOf(newGrade) > GRADE_ORDER.indexOf(oldGrade);
    }
	
	private void syncTotalOrderStatus(Long orderItemNo) {
		Long orderNo = adminOrderItemMapper.findOrderNoByOrderItemNo(orderItemNo);
		List<String> orderItemStatuses = adminOrderItemMapper.findOrderItemStatusesByOrderNo(orderNo);
		String calculatedOrderStatus = calculateOrderStatus(orderItemStatuses);
		adminOrderItemMapper.updateOrderStatus(orderNo, calculatedOrderStatus);
	}

	// 주문서의 아이템에 대해서 각자의 상태가 있기 때문에 이에 대한 order_state 동기화 정책이 필요함
	//주문서의 아이템이
	//	전부 PENDING_PAYMENT → PENDING_PAYMENT
	//	전부 CANCELED → CANCELED
	//	전부 DELIVERED → DELIVERED
	//	하나라도 SHIPPING 있으면 → SHIPPING
	//	하나라도 PREPARING 있으면 → PREPARING
	//	그 외 애매한 섞임 → PAYMENT_COMPLETED
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
