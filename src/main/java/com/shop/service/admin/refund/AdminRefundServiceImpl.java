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
import com.shop.mapper.admin.AdminRefundMapper;
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.mapper.user.PaymentMapper;
import com.shop.mapper.user.ProductOptionMapper;
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
	private final ProductOptionMapper productOptionMapper;

	
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


	@Transactional
	@Override
	public void decideRefund(Long memberNo, Long refundNo, String status) throws Exception {
		
		log.info("refundServiceImpl 진입 refundNo:"+refundNo,"status:"+status);
		//refundNo로 orderNo 가져오기
		Long orderNo = refundMapper.getOrderNoByRefundNo(refundNo);
		
		//주문 식별자 가져오기
		String paymentKey =  paymentMapper.getPaymentKeyByOrderNo(orderNo);
		log.info("paymentKey:" + paymentKey);
		
		//db에서 환불 금액 조회
		Long refundAmount = refundMapper.getRefundAmountByRefundNo(refundNo);
		
		//db에서 환불 수량 조회
		Long refundQuantity = refundMapper.getRefundQuantityByRefundNo(refundNo);
		log.info("refundAmount:" + refundAmount);
		
		//환불하려는 아이템의 order_item_no를 가져와야하는데, refundNo로 가져오면 됨
		Long orderItemNo = refundMapper.getOrderItemNoByRefundNo(refundNo);

		//프론트에서 승인버튼을 눌렀을 때 APPORVED/REJECTED를 넘겨줌, 그에 대한 분기
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
				//5.orderItemNo로 재고를 그 refund_quantity만큼 올려야함
				//refundNo로 refundItem에서 가져온 refund_Quantity(프론트값 신뢰x)
				
				//order_item에 있는 product_option_no 가져와서
				// 그 프로덕트 옵션의 stock을 +quantity하면될듯
				Long productOptionNo= orderItemMapper.getProductOptionNoByOrderItemNo(orderItemNo);
				productOptionMapper.updateQuantityWhileRefunding(productOptionNo,refundQuantity);
				
				
				// 연결되어 있는 리뷰 삭제,종현님이 구현한거 가져옴
				reviewMapper.deleteReviewByOrderItemNo(orderItemNo);
				
				//등급 재산정, 종현님이 구현한거 가져옴
				updateMemberGradeByAmount(memberNo);
				log.info("환불 완료 처리 - 회원 {}번 등급 재산정 완료", memberNo);

				
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