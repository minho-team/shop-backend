package com.shop.domain;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCoupon {
    private Long memberCouponNo;    // 회원 쿠폰 발급 번호
    private Long memberNo;          // 회원 번호
    private Long couponNo;          // 쿠폰 번호
    private String usedYn;          // 사용 여부 (N: 미사용, Y: 사용완료)
    private LocalDateTime usedAt;   // 사용 일시
    private LocalDateTime issuedAt; // 발급 일시
    private LocalDateTime startAt;  // 유효 기간 시작일
    private LocalDateTime endAt;    // 유효 기간 종료일
}