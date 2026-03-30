package com.shop.service.user.roulette;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.MemberCoupon;
import com.shop.mapper.user.RouletteMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RouletteServiceImpl implements RouletteService {

    private final RouletteMapper rouletteMapper;

    @Value("${signup.coupon.no}")
    private Long signupCouponNo;

    // ================================================
    // 룰렛 확률 설정 (1~1000 기준)
    // 꽝      75.0% → 1~750
    // 1,000원 15.0% → 751~900
    // 3,000원  7.0% → 901~970
    // 5,000원  2.5% → 971~995
    // 10,000원 0.5% → 996~1000
    // ※ 마지막 값은 반드시 1000, 배열 길이 = PRIZE_NAMES/COUPON_NOS 길이와 일치
    // ================================================
    private static final int[] PROBABILITIES = { 750, 900, 970, 995, 1000 };

    // 당첨 상품명 (prizeIndex 순서 = 프론트 PRIZES 배열 순서와 반드시 일치)
    private static final String[] PRIZE_NAMES = {
        "꽝", "1,000원 할인", "3,000원 할인", "5,000원 할인", "10,000원 할인"
    };

    // DB coupon 테이블 coupon_no 매핑
    // null = 꽝(쿠폰 없음), 3=1000원, 4=3000원, 5=5000원, 6=10000원
    private static final Long[] COUPON_NOS = { null, 3L, 4L, 5L, 6L };

    // ================================================
    // 룰렛 돌리기
    // - 하루 1회 제한 (roulette_log 테이블 기준)
    // - 꽝이 아니면 쿠폰 발급 (발급일로부터 30일 유효)
    // ================================================
    @Override
    public Map<String, Object> spin(Long memberNo) throws Exception {

        // 오늘 이미 돌렸는지 DB 확인 (TRUNC로 날짜만 비교)
        if (rouletteMapper.countTodaySpin(memberNo) > 0) {
            throw new Exception("오늘은 이미 룰렛을 돌리셨습니다. 내일 다시 도전하세요!");
        }

        // 1~1000 랜덤값으로 당첨 칸 결정
        int rand       = new Random().nextInt(1000) + 1;
        int prizeIndex = 0;
        for (int i = 0; i < PROBABILITIES.length; i++) {
            if (rand <= PROBABILITIES[i]) {
                prizeIndex = i;
                break;
            }
        }

        String prizeName = PRIZE_NAMES[prizeIndex];
        Long   couponNo  = COUPON_NOS[prizeIndex];

        // 결과 로그 저장 (꽝 포함 항상 기록 → 하루 1회 제한 기준)
        rouletteMapper.insertRouletteLog(memberNo, prizeName, couponNo);

        // 꽝이 아니면 member_coupon 테이블에 쿠폰 발급 (30일 유효)
        if (couponNo != null) {
            LocalDateTime now = LocalDateTime.now();
            MemberCoupon mc = MemberCoupon.builder()
                    .memberNo(memberNo)
                    .couponNo(couponNo)
                    .usedYn("N")       // N = 미사용
                    .issuedAt(now)
                    .startAt(now)
                    .endAt(now.plusDays(30))
                    .build();
            rouletteMapper.insertMemberCoupon(mc);
        }

        // 프론트에 전달할 결과 맵
        Map<String, Object> result = new HashMap<>();
        result.put("prizeName",  prizeName);        // 결과 표시용 상품명
        result.put("prizeIndex", prizeIndex);        // 룰렛 애니메이션 칸 인덱스
        result.put("hasCoupon",  couponNo != null);  // 쿠폰 발급 여부
        return result;
    }

    // ================================================
    // 신규가입 웰컴 쿠폰 자동 지급
    // - MemberServiceImpl.register() 에서 가입 완료 후 호출
    // - 3,000원 할인쿠폰 / 365일 유효
    // ================================================
    @Override
    public void issueSignupCoupon(Long memberNo) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        MemberCoupon mc = MemberCoupon.builder()
                .memberNo(memberNo)
                .couponNo(signupCouponNo)
                .usedYn("N")
                .issuedAt(now)
                .startAt(now)
                .endAt(now.plusDays(365))
                .build();
        rouletteMapper.insertMemberCoupon(mc);
    }
}