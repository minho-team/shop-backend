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
public class Coupon {
    private Long couponNo;          // 쿠폰 고유 번호
    private String couponName;      // 쿠폰명
    private String discountType;    // 할인 타입 (RATE: 비율%, FIXED: 고정금액)
    private Long discountValue;     // 할인 값
    private String deleteYn;        // 삭제 여부 (N: 정상, Y: 삭제)
    private LocalDateTime createdAt; // 생성 일시
}