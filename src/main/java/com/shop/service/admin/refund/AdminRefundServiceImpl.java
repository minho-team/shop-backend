package com.shop.service.admin.refund;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.shop.dto.admin.refund.AdminRefundDetailFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundDetailItemDTO;
import com.shop.dto.admin.refund.AdminRefundDetailResponseDTO;
import com.shop.dto.admin.refund.AdminRefundFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundListItemDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundListResponseDTO;
import com.shop.dto.admin.refund.AdminRefundPageResponseDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateResponseDTO;
import com.shop.mapper.admin.AdminRefundMapper;
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.mapper.user.PaymentMapper;
import com.shop.mapper.user.RefundMapper;
import com.shop.mapper.user.ReviewMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

	private final AdminRefundMapper adminRefundMapper;
	// 환불 완료 시 purchaseCount 차감 및 grade 갱신을 위해 주입
	private final MemberService memberService;

	// 환불 완료 시 리뷰 삭제를 위해 ReviewMapper 주입
	private final ReviewMapper reviewMapper;

	private final OrdersMapper ordersMapper;
	private final RefundMapper refundMapper;
	private final OrderItemMapper orderItemMapper;
	private final RestTemplate restTemplate;
	private final PaymentMapper paymentMapper;

	
	@Value("${toss.secret-key}")
	private String tossSecretKey;

	// 환불 리스트 받아오기, 검색어, 페이징 포함
	@Override
	public AdminRefundPageResponseDTO getRefundList(AdminRefundListRequestDTO request) {
		int totalCount = adminRefundMapper.getRefundCount(request);

		int totalPage = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / request.getSize());

		int currentPage = request.getPage();
		if (totalPage > 0 && currentPage > totalPage) {
			currentPage = totalPage;
			request.setPage(currentPage);
		}

		List<AdminRefundFlatRowDTO> flatList = adminRefundMapper.getRefundList(request);

		Map<Long, AdminRefundListResponseDTO> groupedMap = new LinkedHashMap<>();

		for (AdminRefundFlatRowDTO row : flatList) {
			AdminRefundListResponseDTO refundDto = groupedMap.get(row.getRefundNo());

			if (refundDto == null) {
				refundDto = new AdminRefundListResponseDTO();
				refundDto.setRefundNo(row.getRefundNo());
				refundDto.setOrderNo(row.getOrderNo());
				refundDto.setRefundStatus(row.getRefundStatus());
				refundDto.setRequestedAt(row.getRequestedAt());
				refundDto.setMemberId(row.getMemberId());
				refundDto.setName(row.getName());
				refundDto.setTotalRefundAmount(row.getTotalRefundAmount());
				refundDto.setItems(new ArrayList<>());
				groupedMap.put(row.getRefundNo(), refundDto);
			}

			AdminRefundListItemDTO itemDto = new AdminRefundListItemDTO();
			itemDto.setRefundItemNo(row.getRefundItemNo());
			itemDto.setOrderItemNo(row.getOrderItemNo());
			itemDto.setItemName(row.getItemName());
			itemDto.setItemColor(row.getItemColor());
			itemDto.setItemSize(row.getItemSize());
			itemDto.setRefundQuantity(row.getRefundQuantity());
			itemDto.setRefundAmount(row.getRefundAmount());
			itemDto.setRefundItemStatus(row.getRefundItemStatus());

			refundDto.getItems().add(itemDto);
		}

		int blockSize = 5;
		int startPage = ((currentPage - 1) / blockSize) * blockSize + 1;
		int endPage = Math.min(startPage + blockSize - 1, totalPage);

		return AdminRefundPageResponseDTO.builder().list(new ArrayList<>(groupedMap.values())).page(currentPage)
				.size(request.getSize()).totalCount(totalCount).totalPage(totalPage)
				.startPage(totalPage == 0 ? 1 : startPage).endPage(totalPage == 0 ? 1 : endPage).hasPrev(startPage > 1)
				.hasNext(endPage < totalPage).build();
	}

	@Override
	public AdminRefundDetailResponseDTO getRefundDetail(Long refundNo) {
		List<AdminRefundDetailFlatRowDTO> rows = adminRefundMapper.getRefundDetail(refundNo);

		if (rows == null || rows.isEmpty()) {
			throw new IllegalArgumentException("환불 정보를 찾을 수 없습니다.");
		}

		AdminRefundDetailResponseDTO dto = new AdminRefundDetailResponseDTO();
		dto.setRefundNo(rows.get(0).getRefundNo());
		dto.setOrderNo(rows.get(0).getOrderNo());
		dto.setRefundStatus(rows.get(0).getRefundStatus());
		dto.setRequestedAt(rows.get(0).getRequestedAt());
		dto.setMemberId(rows.get(0).getMemberId());
		dto.setName(rows.get(0).getName());
		dto.setTotalRefundAmount(rows.get(0).getTotalRefundAmount());
		dto.setRefundReason(rows.get(0).getRefundReason());
		dto.setBankCode(rows.get(0).getBankCode());
		dto.setBankName(rows.get(0).getBankName());
		dto.setItems(new ArrayList<>());

		for (AdminRefundDetailFlatRowDTO row : rows) {
			AdminRefundDetailItemDTO itemDto = new AdminRefundDetailItemDTO();
			itemDto.setRefundItemNo(row.getRefundItemNo());
			itemDto.setOrderItemNo(row.getOrderItemNo());
			itemDto.setItemName(row.getItemName());
			itemDto.setItemColor(row.getItemColor());
			itemDto.setItemSize(row.getItemSize());
			itemDto.setRefundQuantity(row.getRefundQuantity());
			itemDto.setRefundAmount(row.getRefundAmount());
			itemDto.setRefundItemStatus(row.getRefundItemStatus());

			dto.getItems().add(itemDto);
		}

		return dto;
	}

	// 환불 상태 변경
	@Override
	@Transactional
	public AdminRefundStatusUpdateResponseDTO updateRefundStatus(Long refundNo,
			AdminRefundStatusUpdateRequestDTO request) {
		String refundStatus = request.getStatus();

		validateRefundStatus(refundStatus);

		int exists = adminRefundMapper.existsRefund(refundNo);
		if (exists == 0) {
			throw new IllegalArgumentException("환불 정보를 찾을 수 없습니다.");
		}

		// 1. 상태 동기화 로직 시작
		if ("REQUESTED".equals(refundStatus)) {
			adminRefundMapper.updateRefundItemsStatus(refundNo, "REQUESTED");
			adminRefundMapper.updateRefundHeaderStatus(refundNo, "REQUESTED");
			adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUND_REQUESTED");
			adminRefundMapper.updateRefundHeaderTime(refundNo, refundStatus);

		} else if ("APPROVED".equals(refundStatus)) {
			adminRefundMapper.updateRefundItemsStatus(refundNo, "APPROVED");
			adminRefundMapper.updateRefundHeaderStatus(refundNo, "APPROVED");
			adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUND_APPROVED");
			adminRefundMapper.updateRefundHeaderTime(refundNo, refundStatus);

		} else if ("REJECTED".equals(refundStatus)) {
			adminRefundMapper.updateRefundItemsStatus(refundNo, "REJECTED");
			adminRefundMapper.updateRefundHeaderStatus(refundNo, "REJECTED");
			adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REJECTED");
			adminRefundMapper.updateRefundHeaderTime(refundNo, refundStatus);

		} else if ("COMPLETED".equals(refundStatus)) {

			// 상태를 바꾸기 '전'에 먼저 환불 상세 데이터
			List<AdminRefundDetailFlatRowDTO> rows = adminRefundMapper.getRefundDetail(refundNo);

			if (rows != null && !rows.isEmpty()) {
				// 가져온 데이터를 바탕으로 리뷰를 먼저 삭제
				for (AdminRefundDetailFlatRowDTO row : rows) {
					if (row.getOrderItemNo() != null) {
						reviewMapper.deleteReviewByOrderItemNo(row.getOrderItemNo());
						log.info("환불 승인 완료 - 연결된 리뷰 삭제 처리됨 (orderItemNo: {})", row.getOrderItemNo());
					}
				}

				// 회원 등급 및 구매 횟수 조정
				// 등급 하향 조정을 위한 재산정
				Long memberNo = rows.get(0).getMemberNo();
				if (memberNo != null) {
					try {
						updateMemberGradeByAmount(memberNo);
						log.info("환불 완료 처리 - 회원 {}번 등급 재산정 완료", memberNo);
					} catch (Exception e) {
						log.error("회원 데이터 갱신 실패: {}", e.getMessage());
						throw new RuntimeException("회원 등급 수정 중 오류 발생", e);
					}
				}
			}

			// 모든 처리가 끝난 후 마지막에 DB 상태를 'COMPLETED' 변경
			adminRefundMapper.updateRefundItemsStatus(refundNo, "COMPLETED");
			adminRefundMapper.updateRefundHeaderStatus(refundNo, "COMPLETED");
			adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUNDED");
			adminRefundMapper.updateRefundHeaderTime(refundNo, refundStatus);
		}

		// 최종 헤더 상태 확인 및 반환 데이터 구성
		String finalHeaderStatus;
		int notCompletedCount = adminRefundMapper.countNotCompletedRefundItems(refundNo);

		if (notCompletedCount == 0) {
			finalHeaderStatus = "COMPLETED";
			adminRefundMapper.updateRefundHeaderStatus(refundNo, "COMPLETED");
			adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUNDED");
		} else {
			// 상세 정보를 다시 조회하여 현재 헤더 상태를 가져옴
			List<AdminRefundDetailFlatRowDTO> finalRows = adminRefundMapper.getRefundDetail(refundNo);
			finalHeaderStatus = finalRows.get(0).getRefundStatus();
		}

		return AdminRefundStatusUpdateResponseDTO.builder().refundNo(refundNo).refundStatus(finalHeaderStatus)
				.message("환불 상태가 변경되었습니다.").build();
	}

	private void updateMemberGradeByAmount(Long memberNo) throws Exception {
		// 1. 배송완료(DELIVERED) 상태인 총 금액 합산 조회
		long totalAmount = ordersMapper.selectTotalPurchaseAmount(memberNo);

		// 2. 금액별 등급 판별
		String newGrade = "BASIC";
		if (totalAmount >= 1000000)
			newGrade = "VVIP";
		else if (totalAmount >= 500000)
			newGrade = "VIP";
		else if (totalAmount >= 300000)
			newGrade = "GOLD";
		else if (totalAmount >= 100000)
			newGrade = "SILVER";

		// 3. DB에 직접 반영
		memberService.updateMemberGradeDirectly(memberNo, newGrade);
	}

	/**
	 * 환불 상태값 유효성 검증 메서드
	 * 
	 * @param refundStatus 입력받은 상태값
	 */
	private void validateRefundStatus(String refundStatus) {
		if (!"REQUESTED".equals(refundStatus) && !"APPROVED".equals(refundStatus) && !"REJECTED".equals(refundStatus)
				&& !"COMPLETED".equals(refundStatus)) {
			throw new IllegalArgumentException("허용되지 않는 환불 상태입니다: " + refundStatus);
		}
	}

	@Override
	@Transactional
	public void decideRefund(Long refundNo, String status) throws Exception {
		
		log.info("refundServiceImpl 진입 refundNo:"+refundNo,"status:"+status);
		//refundNo로 orderNo 가져오기
		Long orderNo = refundMapper.getOrderNoByRefundNo(refundNo);
		
		String paymentKey =  paymentMapper.getPaymentKeyByOrderNo(orderNo);
		log.info("paymentKey:" + paymentKey);
		//db에서 환불 금액 조회
		Long refundAmount = refundMapper.getRefundAmountByRefundNo(refundNo);
		log.info("refundAmount:" + refundAmount);
		
		//환불하려는 아이템의 order_item_no를 가져와야하는데, refundNo로 가져오면 됨
		Long orderItemNo = refundMapper.getOrderItemNoByRefundNo(refundNo);

		
		if(status.equals("APPROVED")){
			
			// 0.승인 버튼 누르는 순간 approved_at 기록
			refundMapper.updateApprovedAt(refundNo);
			
			//1.orderItem상태 변경
			orderItemMapper.updateOrderItemStatusByOrderItemNo(orderItemNo, "REFUND_REQUESTED");
			//2.refunD상태변경 상태 approved로 변경
			refundMapper.updateRefundStatus(refundNo,"APPROVED");
			//3.refund_item상태 approved로 변경
			refundMapper.updateRefundItemStatus(refundNo,"APPROVED");
			try {
				callTossRefund(paymentKey, refundAmount);
				//1.orderItem상태 변경
				orderItemMapper.updateOrderItemStatusByOrderItemNo(orderItemNo, "REFUNDED");
				//2.refunD상태변경 상태 COMPLETED로 변경
				refundMapper.updateRefundStatus(refundNo,"COMPLETED");
				//3.refund_item상태 COMPLETED로 변경
				refundMapper.updateRefundItemStatus(refundNo,"COMPLETED");
				//4.완료 시각 기록
				refundMapper.updateCompletedAt(refundNo);

				
			}catch(Exception e) {
				
				e.printStackTrace();
				log.info("Toss환불 api 호출 로직 중 에러 발생");
				//1.orderItem상태 변경
				orderItemMapper.updateOrderItemStatusByOrderItemNo(orderItemNo, "REFUND_FAILED");
				//2.refunD상태변경 상태 REFUND_FAILED로 변경
				refundMapper.updateRefundStatus(refundNo,"REFUND_FAILED");
				//3.refund_item상태 REFUND_FAILED로 변경
				refundMapper.updateRefundItemStatus(refundNo,"REFUND_FAILED");
			}
			
		}else {
			//승인 거절 시
			orderItemMapper.updateOrderItemStatusByOrderItemNo(orderItemNo, "REJECTED");
			//2.refunD상태변경 상태 approved로 변경
			refundMapper.updateRefundStatus(refundNo,"REJECTED");
			//3.refund_item상태 approved로 변경
			refundMapper.updateRefundItemStatus(refundNo,"REJECTED");
			//4.거절 시각 기록
			refundMapper.updateRejectedAt(refundNo);
			
		}
			
	}
	
	
	private void callTossRefund(String paymentKey, long cancelAmount) throws Exception{

	    String encodedAuth = Base64.getEncoder()
	        .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Basic " + encodedAuth);
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    Map<String, Object> body = new HashMap<>();
	    body.put("cancelReason", "관리자 환불 승인");
	    body.put("cancelAmount", cancelAmount);

	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

	    restTemplate.exchange(
	        "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel",
	        HttpMethod.POST,
	        entity,
	        Map.class
	    );
	}
	
	
	
	
	
	
	
	
	
	
}