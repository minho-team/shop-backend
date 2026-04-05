package com.shop.service.user.payment;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.shop.domain.Member;
import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.domain.Payment;
import com.shop.dto.user.payment.PaymentConfirmRequestDTO;
import com.shop.dto.user.payment.PaymentConfirmResponseDTO;
import com.shop.dto.user.payment.PaymentPrepareItemDTO;
import com.shop.dto.user.payment.PaymentPrepareRequestDTO;
import com.shop.dto.user.payment.PaymentPrepareResponseDTO;
import com.shop.mapper.admin.AdminMemberMapper;
import com.shop.mapper.user.CartItemMapper;
import com.shop.mapper.user.MemberMapper;
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.mapper.user.PaymentMapper;
import com.shop.mapper.user.ProductOptionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final MemberMapper memberMapper;
	private final OrdersMapper ordersMapper;
	private final OrderItemMapper orderItemMapper;
	private final PaymentMapper paymentMapper;
	private final CartItemMapper cartItemMapper;
	private final ProductOptionMapper productOptionMapper;
	private final AdminMemberMapper adminMemberMapper;
	private final RestTemplate restTemplate;
	

	@Value("${toss.secret-key}")
	private String tossSecretKey;

	@Override
	@Transactional
	public PaymentPrepareResponseDTO preparePayment(String memberId, PaymentPrepareRequestDTO request) {
		Member member = null;
		try {
			member = memberMapper.readOneMember(memberId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (member == null) {
			throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
		}

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("주문 상품이 없습니다.");
		}

		long recalculatedTotal = 0L;
		List<Long> validatedUnitPrices = new ArrayList<>();
		for (PaymentPrepareItemDTO item : request.getItems()) {
			Map<String, Object> validationItem = productOptionMapper.selectPaymentValidationItem(item.getProductOptionNo());
			if (validationItem == null) {
				throw new IllegalArgumentException("유효하지 않은 상품 옵션입니다.");
			}

			String productUseYn = String.valueOf(validationItem.get("productUseYn"));
			String optionUseYn = String.valueOf(validationItem.get("optionUseYn"));
			long stock = Long.parseLong(String.valueOf(validationItem.get("stock")));
			long actualUnitPrice = Long.parseLong(String.valueOf(validationItem.get("salePrice")));

			if (!"Y".equals(productUseYn) || !"Y".equals(optionUseYn)) {
				throw new IllegalArgumentException("판매 중이 아닌 상품이 포함되어 있습니다.");
			}

			if (stock < item.getQuantity()) {
				throw new IllegalArgumentException("재고가 부족한 상품이 포함되어 있습니다.");
			}

			// 프론트가 보낸 주문상품 단가와 서버가 계산한 실제 판매가를 교차검증
			if (!Objects.equals(actualUnitPrice, item.getUnitPrice())) {
				throw new IllegalArgumentException("주문 상품 금액이 올바르지 않습니다.");
			}

			validatedUnitPrices.add(actualUnitPrice);
			recalculatedTotal += (long) item.getQuantity() * actualUnitPrice;
		}

		// 쿠폰 할인 계산
		long discountAmount = 0L;
		Long memberCouponNo = request.getMemberCouponNo();
		if (memberCouponNo != null) {
			Map<String, Object> coupon = adminMemberMapper.selectMemberCouponForUse(memberCouponNo, member.getMemberNo());
			if (coupon == null) {
				throw new IllegalArgumentException("사용할 수 없는 쿠폰입니다.");
			}
			String discountType = String.valueOf(coupon.get("discountType"));
			long discountValue = Long.parseLong(String.valueOf(coupon.get("discountValue")));
			// FIXED: 정액 할인 / RATE·PERCENT: 정률 할인
			// ★ PERCENT는 관리자가 쿠폰 생성 시 프론트에서 보낼 수 있는 별칭 값으로 RATE와 동일 처리
			if ("FIXED".equals(discountType)) {
				discountAmount = discountValue;
			} else if ("RATE".equals(discountType) || "PERCENT".equals(discountType)) {
				discountAmount = recalculatedTotal * discountValue / 100;
			}
			discountAmount = Math.min(discountAmount, recalculatedTotal);
		}

		long finalPrice = recalculatedTotal - discountAmount;

		if (!Objects.equals(finalPrice, request.getTotalPrice())) {
			throw new IllegalArgumentException("주문 금액이 올바르지 않습니다.");
		}

		// =========================================================
		// [결제 대기시] 기존 주문 번호(orderNo) 존재 여부에 따른 분기 처리
		// =========================================================
		Long existingOrderNo = request.getOrderNo();
		Orders order;

		if (existingOrderNo != null && existingOrderNo > 0) {
			// [재결제] 기존 주문 정보 가져오기
			order = ordersMapper.getOneOrder(existingOrderNo);
			if (order == null) throw new IllegalArgumentException("존재하지 않는 주문 번호입니다.");

			// 기존 상품 내역 지우기 (중복 삽입 방지)
			orderItemMapper.deleteByOrderNo(existingOrderNo);

			// 배송지 및 금액 최신화
			order.setOrdererName(request.getOrdererName());
			order.setOrdererPhoneNumber(request.getOrdererPhoneNumber());
			order.setOrdererEmail(request.getOrdererEmail());
			// 할인 전 주문금액과 실제 쿠폰 할인금액을 함께 저장
			order.setOrderAmount(recalculatedTotal);
			order.setCouponDiscountAmount(discountAmount);
			order.setTotalPrice(finalPrice);
			order.setReceiverName(request.getReceiverName());
			order.setReceiverPhoneNumber(request.getReceiverPhoneNumber());
			order.setReceiverZipCode(request.getReceiverZipCode());
			order.setReceiverBaseAddress(request.getReceiverBaseAddress());
			order.setReceiverDetailAddress(request.getReceiverDetailAddress());
			order.setMessage(request.getMessage());

			// DB 업데이트 (INSERT가 아닌 UPDATE 호출)
			ordersMapper.updateOrder(order);

		} else {
			// [신규 결제] 시퀀스 작동하여 새 번호 생성
			order = new Orders();
			order.setMemberNo(member.getMemberNo());
			order.setOrdererName(request.getOrdererName());
			order.setOrdererPhoneNumber(request.getOrdererPhoneNumber());
			order.setOrdererEmail(request.getOrdererEmail());
			order.setOrderStatus("PENDING_PAYMENT");
			// 신규 주문도 동일한 금액 정책으로 저장
			order.setOrderAmount(recalculatedTotal);
			order.setCouponDiscountAmount(discountAmount);
			order.setTotalPrice(finalPrice);
			order.setReceiverName(request.getReceiverName());
			order.setReceiverPhoneNumber(request.getReceiverPhoneNumber());
			order.setReceiverZipCode(request.getReceiverZipCode());
			order.setReceiverBaseAddress(request.getReceiverBaseAddress());
			order.setReceiverDetailAddress(request.getReceiverDetailAddress());
			order.setMessage(request.getMessage());

			ordersMapper.createOrder(order);
		}

		// [주의] 토스 결제는 재시도 시 pgOrderId가 중복되면 에러 가능성. 
		// 기존 주문이더라도 뒤에 붙는 시간값을 갱신하여 새 pgOrderId를 부여.
		String pgOrderId = "ORDER_" + order.getOrderNo() + "_" + System.currentTimeMillis();
		ordersMapper.updatePgOrderId(order.getOrderNo(), pgOrderId);

		long remainingDiscount = discountAmount;

		for (int i = 0; i < request.getItems().size(); i++) {
			PaymentPrepareItemDTO dto = request.getItems().get(i);
			long actualUnitPrice = validatedUnitPrices.get(i);
			// 현재 주문상품의 금액 = 주문 시점 단가 * 수량
			long itemAmount = (long) dto.getQuantity() * actualUnitPrice;
			long allocatedDiscount;

			// 할인금액이 없거나 총 주문금액이 0원이면 상품별 배분 할인금액도 0원으로 처리
			if (discountAmount <= 0L || recalculatedTotal <= 0L) {
				allocatedDiscount = 0L;
			// 마지막 상품은 남은 할인금액 전부를 배분하여 정수 나눗셈 오차를 보정
			} else if (i == request.getItems().size() - 1) {
				allocatedDiscount = remainingDiscount;
			} else {
				// 주문 전체 금액 대비 현재 상품 금액 비율만큼 쿠폰 할인금액을 배분
				allocatedDiscount = (discountAmount * itemAmount) / recalculatedTotal;
				remainingDiscount -= allocatedDiscount;
			}

			Long productOptionNo = dto.getProductOptionNo();
			Integer quantity = dto.getQuantity();
			
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(order.getOrderNo()); // 기존 주문 번호 유지
			orderItem.setProductOptionNo(dto.getProductOptionNo());
			orderItem.setQuantity(dto.getQuantity());
			orderItem.setUnitPrice(actualUnitPrice);
			// 환불/정산 시 사용할 주문상품별 쿠폰 배분 할인금액 저장
			orderItem.setCouponDiscountAmount(allocatedDiscount);
			orderItem.setItemName(dto.getItemName());
			orderItem.setItemSize(dto.getItemSize());
			orderItem.setItemColor(dto.getItemColor());
			orderItem.setImageUrl(dto.getImageUrl());
			orderItem.setOrderItemStatus("PENDING_PAYMENT");
			
			orderItemMapper.deductStock(productOptionNo,quantity);
			orderItemMapper.insertOrderItem(orderItem);
		}

		Payment payment = new Payment();
		payment.setOrderNo(order.getOrderNo());
		payment.setMemberNo(member.getMemberNo());
		payment.setPaymentMethod("CARD");
		payment.setPaymentStatus("READY");
		// 결제 이력은 최종 결제금액 기준으로 저장
		payment.setPaymentAmount(finalPrice);
		// 결제 시 반영된 쿠폰 할인금액 저장
		payment.setDiscountAmount(discountAmount);
		payment.setPgProvider("TOSS");
		paymentMapper.insertReadyPayment(payment);

		String orderName = request.getItems().size() == 1 ? request.getItems().get(0).getItemName()
				: request.getItems().get(0).getItemName() + " 외 " + (request.getItems().size() - 1) + "건";

		return PaymentPrepareResponseDTO.builder().orderNo(order.getOrderNo()).orderId(pgOrderId).orderName(orderName)
				.amount(finalPrice).customerName(request.getOrdererName())
				.customerEmail(request.getOrdererEmail()).customerMobilePhone(request.getOrdererPhoneNumber()).build();
	}

	@Override
	@Transactional
	public PaymentConfirmResponseDTO confirmPayment(String memberId, PaymentConfirmRequestDTO request) {
		
		Member member = null;
		
		try {
			member = memberMapper.readOneMember(memberId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (member == null) {
			throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
		}

		Orders order = ordersMapper.findByPgOrderId(request.getOrderId());
		if (order == null) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}

		if (!order.getMemberNo().equals(member.getMemberNo())) {
			throw new IllegalArgumentException("본인 주문만 결제할 수 있습니다.");
		}

		if (!Objects.equals(order.getTotalPrice(), request.getAmount())) {
			throw new IllegalArgumentException("결제 금액 검증에 실패했습니다.");
		}

		String encodedAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encodedAuth);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = new HashMap<>();
		body.put("paymentKey", request.getPaymentKey());
		body.put("orderId", request.getOrderId());
		body.put("amount", request.getAmount());

		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.exchange("https://api.tosspayments.com/v1/payments/confirm",
				HttpMethod.POST, httpEntity, Map.class);

		Map result = response.getBody();
		if (result == null) {
			throw new IllegalArgumentException("토스 승인 응답이 비어 있습니다.");
		}

		String paymentKey = String.valueOf(result.get("paymentKey"));

		//payment의 status completed로 변경
		paymentMapper.completePayment(order.getOrderNo(), paymentKey, paymentKey);
		
		//orders의 status를 PAYMENT_COMPLETED로 변경
		ordersMapper.updateOrderStatus(order.getOrderNo(), "PAYMENT_COMPLETED");
		//order_item의 status를 PAYMENT_COMPLETED로 변경
		orderItemMapper.updateOrderItemStatusByOrderNo(order.getOrderNo(), "PAYMENT_COMPLETED");

		// 쿠폰 사용 처리 (결제 확정 시 member_coupon.used_yn = 'Y')
		if (request.getMemberCouponNo() != null) {
			adminMemberMapper.updateMemberCouponUsed(request.getMemberCouponNo());
		}
		
		// 장바구니에서 주문한 상품만 결제 완료 후 장바구니에서 삭제
		List<Long> orderedCartItemNos = request.getOrderedCartItemNos();
		if (orderedCartItemNos != null && !orderedCartItemNos.isEmpty()) {
			for (Long cartItemNo : orderedCartItemNos) {
				try {
					cartItemMapper.deleteCartItemByMemberNoAndCartItemNo(member.getMemberNo(), cartItemNo);
				} catch (Exception e) {
					throw new RuntimeException("장바구니 상품 삭제 중 오류가 발생했습니다.", e);
				}
			}
		}

		List<PaymentPrepareItemDTO> items = orderItemMapper.selectPaymentResultItemsByOrderNo(order.getOrderNo());

		return PaymentConfirmResponseDTO.builder().orderNo(order.getOrderNo()).amount(request.getAmount())
				.ordererName(order.getOrdererName()).approvedAt(LocalDateTime.now()).items(items).build();
	}

	// ================================================
	// 0원 결제 확정 (쿠폰으로 전액 할인된 주문 처리)
	// - 쿠폰 할인액 >= 상품 금액 → 결제금액 0원 → Toss SDK 호출 불가
	// - Toss API 호출을 건너뛰고 주문 상태만 직접 PAYMENT_COMPLETED로 변경
	// - confirmPayment와 동일한 후처리(쿠폰 사용, 장바구니 삭제) 수행
	// ================================================
	@Override
	@Transactional
	public PaymentConfirmResponseDTO confirmFreePayment(String memberId, PaymentConfirmRequestDTO request) {
		// 1. 회원 조회
		Member member = null;
		try {
			member = memberMapper.readOneMember(memberId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (member == null) {
			throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
		}

		// 2. pgOrderId로 주문 조회
		Orders order = ordersMapper.findByPgOrderId(request.getOrderId());
		if (order == null) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}

		// 3. 본인 주문 여부 검증
		if (!order.getMemberNo().equals(member.getMemberNo())) {
			throw new IllegalArgumentException("본인 주문만 결제할 수 있습니다.");
		}

		// 4. 실제 0원 주문인지 검증 (비정상 호출 방어)
		if (order.getTotalPrice() != 0L) {
			throw new IllegalArgumentException("0원 결제가 아닙니다.");
		}

		// 5. 결제 레코드를 완료 상태로 변경 (paymentKey = "FREE_PAYMENT" 고정)

		// 6. 주문 및 주문 아이템 상태 → PAYMENT_COMPLETED
		ordersMapper.updateOrderStatus(order.getOrderNo(), "PAYMENT_COMPLETED");
		orderItemMapper.updateOrderItemStatusByOrderNo(order.getOrderNo(), "PAYMENT_COMPLETED");

		// 7. 사용한 쿠폰 사용 처리 (used_yn = 'Y')
		if (request.getMemberCouponNo() != null) {
			adminMemberMapper.updateMemberCouponUsed(request.getMemberCouponNo());
		}

		// 8. 장바구니에서 주문한 상품 삭제
		List<Long> orderedCartItemNos = request.getOrderedCartItemNos();
		if (orderedCartItemNos != null && !orderedCartItemNos.isEmpty()) {
			for (Long cartItemNo : orderedCartItemNos) {
				try {
					cartItemMapper.deleteCartItemByMemberNoAndCartItemNo(member.getMemberNo(), cartItemNo);
				} catch (Exception e) {
					throw new RuntimeException("장바구니 상품 삭제 중 오류가 발생했습니다.", e);
				}
			}
		}

		// 9. 주문 결과 아이템 조회 후 응답 반환
		List<PaymentPrepareItemDTO> items = orderItemMapper.selectPaymentResultItemsByOrderNo(order.getOrderNo());

		return PaymentConfirmResponseDTO.builder().orderNo(order.getOrderNo()).amount(0L)
				.ordererName(order.getOrdererName()).approvedAt(LocalDateTime.now()).items(items).build();
	}
}
