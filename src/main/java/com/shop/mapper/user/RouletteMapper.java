package com.shop.mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.MemberCoupon;

@Mapper
public interface RouletteMapper {

    // 오늘 이미 돌렸는지 확인
    int countTodaySpin(@Param("memberNo") Long memberNo);

    // 룰렛 로그 저장
    void insertRouletteLog(@Param("memberNo") Long memberNo,
                           @Param("prizeName") String prizeName,
                           @Param("couponNo") Long couponNo);

    // 쿠폰 발급
    void insertMemberCoupon(MemberCoupon memberCoupon);
}