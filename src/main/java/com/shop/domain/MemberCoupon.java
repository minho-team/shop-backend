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
public class MemberCoupon {

	private Long memberCouponNo; // 회원 쿠폰 번호 (PK)
	private Long memberNo; // 회원 번호 (FK)
	private Long couponNo; // 쿠폰 번호 (FK)
	private String usedYn; // 사용 여부 (N=미사용, Y=사용)
	private LocalDateTime usedAt; // 사용 일시
	private LocalDateTime issuedAt; // 발급 일시
	private LocalDateTime startAt; // 유효 시작일 (발급 시 지정, member_coupon 컬럼)
	private LocalDateTime endAt; // 유효 종료일 (발급 시 지정, member_coupon 컬럼)

	// 쿠폰 상세 정보 (coupon 테이블 JOIN)
	private String couponName; // 쿠폰명
	private String discountType; // 할인 유형 (RATE/FIXED)
	private int discountValue; // 할인 값
}