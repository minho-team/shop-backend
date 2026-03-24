package com.shop.service.user.payment;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
import com.shop.mapper.user.CartItemMapper;
import com.shop.mapper.user.MemberMapper;
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.mapper.user.PaymentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final MemberMapper memberMapper;
	private final OrdersMapper ordersMapper;
	private final OrderItemMapper orderItemMapper;
	private final PaymentMapper paymentMapper;
	private final CartItemMapper cartItemMapper;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (member == null) {
			throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
		}

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("주문 상품이 없습니다.");
		}

		long recalculatedTotal = 0L;
		for (PaymentPrepareItemDTO item : request.getItems()) {
			recalculatedTotal += (long) item.getQuantity() * item.getUnitPrice();
		}

		if (!Objects.equals(recalculatedTotal, request.getTotalPrice())) {
			throw new IllegalArgumentException("주문 금액이 올바르지 않습니다.");
		}

		Orders order = new Orders();
		order.setMemberNo(member.getMemberNo());
		order.setOrdererName(request.getOrdererName());
		order.setOrdererPhoneNumber(request.getOrdererPhoneNumber());
		order.setOrdererEmail(request.getOrdererEmail());
		order.setOrderStatus("PENDING_PAYMENT");
		order.setTotalPrice(recalculatedTotal);
		order.setReceiverName(request.getReceiverName());
		order.setReceiverPhoneNumber(request.getReceiverPhoneNumber());
		order.setReceiverZipCode(request.getReceiverZipCode());
		order.setReceiverBaseAddress(request.getReceiverBaseAddress());
		order.setReceiverDetailAddress(request.getReceiverDetailAddress());
		order.setMessage(request.getMessage());

		ordersMapper.createOrder(order);

		String pgOrderId = "ORDER_" + order.getOrderNo() + "_" + System.currentTimeMillis();
		ordersMapper.updatePgOrderId(order.getOrderNo(), pgOrderId);

		for (PaymentPrepareItemDTO dto : request.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(order.getOrderNo());
			orderItem.setProductOptionNo(dto.getProductOptionNo());
			orderItem.setQuantity(dto.getQuantity());
			orderItem.setUnitPrice(dto.getUnitPrice());
			orderItem.setItemName(dto.getItemName());
			orderItem.setItemSize(dto.getItemSize());
			orderItem.setItemColor(dto.getItemColor());
			orderItem.setImageUrl(dto.getImageUrl());
			orderItem.setOrderItemStatus("PENDING_PAYMENT");
			orderItemMapper.insertOrderItem(orderItem);
		}

		Payment payment = new Payment();
		payment.setOrderNo(order.getOrderNo());
		payment.setMemberNo(member.getMemberNo());
		payment.setPaymentMethod("CARD");
		payment.setPaymentStatus("READY");
		payment.setPaymentAmount(recalculatedTotal);
		payment.setPgProvider("TOSS");
		paymentMapper.insertReadyPayment(payment);

		String orderName = request.getItems().size() == 1 ? request.getItems().get(0).getItemName()
				: request.getItems().get(0).getItemName() + " 외 " + (request.getItems().size() - 1) + "건";

		return PaymentPrepareResponseDTO.builder().orderNo(order.getOrderNo()).orderId(pgOrderId).orderName(orderName)
				.amount(recalculatedTotal).customerName(request.getOrdererName())
				.customerEmail(request.getOrdererEmail()).customerMobilePhone(request.getOrdererPhoneNumber()).build();
	}

	@Override
	@Transactional
	public PaymentConfirmResponseDTO confirmPayment(String memberId, PaymentConfirmRequestDTO request) {
		Member member = null;
		try {
			member = memberMapper.readOneMember(memberId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

		String method = result.get("method") == null ? "CARD" : String.valueOf(result.get("method"));
		String paymentKey = String.valueOf(result.get("paymentKey"));

		paymentMapper.completePayment(order.getOrderNo(), paymentKey, paymentKey);

		ordersMapper.updateOrderStatus(order.getOrderNo(), "PAYMENT_COMPLETED");
		orderItemMapper.updateOrderItemStatusByOrderNo(order.getOrderNo(), "PAYMENT_COMPLETED");

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
}