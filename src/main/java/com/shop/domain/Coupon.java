package com.shop.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

	private Long couponNo; // 쿠폰 번호 (PK, seq_coupon)
	private String couponName; // 쿠폰 이름
	private String discountType; // RATE(비율%) / FIXED(고정금액)
	private int discountValue; // 할인 값 (비율이면 %, 고정이면 원)
	private String deleteYn; // 삭제 여부 (N=정상, Y=삭제)
	private LocalDateTime createdAt; // 생성 일시
}