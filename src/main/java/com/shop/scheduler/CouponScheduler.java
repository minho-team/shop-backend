package com.shop.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shop.mapper.CouponMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

	private final CouponMapper couponMapper;

	// 매일 자정(00:00:00) 실행
	// end_at 이 지난 쿠폰을 자동으로 소프트 삭제 처리 (delete_yn = 'Y', N=정상 Y=삭제)
	@Scheduled(cron = "0 0 0 * * *")
	public void expireCoupons() {
		try {
			couponMapper.updateExpiredCoupons();
			log.info("[CouponScheduler] 만료 쿠폰 소프트 삭제 완료");
		} catch (Exception e) {
			log.error("[CouponScheduler] 만료 쿠폰 처리 중 오류 발생", e);
		}
	}
}