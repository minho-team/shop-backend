package com.shop.service.user.roulette;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    // ================================================
    // 룰렛 확률 설정 (1~1000 기준)
    // 꽝    50.0% → 1~500
    // 500원  25.0% → 501~750
    // 1000원 15.0% → 751~900
    // 3000원  7.0% → 901~970
    // 5000원  2.5% → 971~995
    // 10000원 0.5% → 996~1000
    // ================================================
    private static final int[] PROBABILITIES = { 500, 750, 900, 970, 995, 1000 };

    // 룰렛 당첨 상품명 (prizeIndex 순서와 일치)
    private static final String[] PRIZE_NAMES = {
        "꽝", "500원 할인", "1,000원 할인", "3,000원 할인", "5,000원 할인", "10,000원 할인"
    };

    // DB coupon 테이블 번호 (SQL 조회 결과 기준)
    // null = 꽝, 3=500원, 4=1000원, 5=3000원, 6=5000원, 7=10000원
    private static final Long[] COUPON_NOS = { null, 3L, 4L, 5L, 6L, 7L };

    // 신규가입 쿠폰 번호 (coupon_no = 2)
    private static final Long SIGNUP_COUPON_NO = 2L;

    // ================================================
    // 룰렛 돌리기
    // - 하루 1회 제한
    // - 꽝이 아니면 쿠폰 발급 (30일 유효)
    // ================================================
    @Override
    public Map<String, Object> spin(Long memberNo) throws Exception {

        // 오늘 이미 돌렸는지 확인
        if (rouletteMapper.countTodaySpin(memberNo) > 0) {
            throw new Exception("오늘은 이미 룰렛을 돌리셨습니다. 내일 다시 도전하세요!");
        }

        // 1~1000 랜덤 값으로 당첨 칸 결정
        int rand = new Random().nextInt(1000) + 1;
        int prizeIndex = 0;
        for (int i = 0; i < PROBABILITIES.length; i++) {
            if (rand <= PROBABILITIES[i]) {
                prizeIndex = i;
                break;
            }
        }

        String prizeName = PRIZE_NAMES[prizeIndex];
        Long couponNo    = COUPON_NOS[prizeIndex];

        // 룰렛 결과 로그 저장 (꽝 포함)
        rouletteMapper.insertRouletteLog(memberNo, prizeName, couponNo);

        // 꽝이 아니면 쿠폰 발급 (30일 유효)
        if (couponNo != null) {
            LocalDateTime now = LocalDateTime.now();
            MemberCoupon mc = MemberCoupon.builder()
                    .memberNo(memberNo)
                    .couponNo(couponNo)
                    .usedYn("N")
                    .issuedAt(now)
                    .startAt(now)
                    .endAt(now.plusDays(30))
                    .build();
            rouletteMapper.insertMemberCoupon(mc);
        }

        // 프론트에 전달할 결과
        Map<String, Object> result = new HashMap<>();
        result.put("prizeName",  prizeName);       // 당첨 상품명
        result.put("prizeIndex", prizeIndex);       // 룰렛 칸 인덱스 (애니메이션용)
        result.put("hasCoupon",  couponNo != null); // 쿠폰 발급 여부
        return result;
    }

    // ================================================
    // 신규가입 쿠폰 자동 지급
    // - 회원가입 완료 시 MemberServiceImpl.register()에서 호출
    // - 3,000원 할인쿠폰 365일 유효
    // ================================================
    @Override
    public void issueSignupCoupon(Long memberNo) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        MemberCoupon mc = MemberCoupon.builder()
                .memberNo(memberNo)
                .couponNo(SIGNUP_COUPON_NO)
                .usedYn("N")
                .issuedAt(now)
                .startAt(now)
                .endAt(now.plusDays(365))
                .build();
        rouletteMapper.insertMemberCoupon(mc);
    }
}