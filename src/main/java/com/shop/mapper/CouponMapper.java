package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Coupon;
import com.shop.domain.MemberCoupon;

public interface CouponMapper {

	// 쿠폰 생성
	void insertCoupon(Coupon coupon) throws Exception;

	// 쿠폰 전체 목록 조회 (delete_yn = 'N' 인 것만)
	List<Coupon> selectAllCoupons() throws Exception;

	// 쿠폰 단건 조회
	Coupon selectCouponByNo(@Param("couponNo") Long couponNo) throws Exception;

	// 쿠폰 소프트 삭제 (delete_yn = 'Y', N=정상 Y=삭제)
	void updateCouponDeleteYn(@Param("couponNo") Long couponNo, @Param("deleteYn") String deleteYn) throws Exception;

	// 쿠폰 하드 삭제 (발급된 쿠폰이 없을 때만 삭제 가능 - FK 제약)
	void deleteCoupon(@Param("couponNo") Long couponNo) throws Exception;

	// end_at이 지난 쿠폰을 자동으로 delete_yn=Y 소프트 삭제 (스케줄러에서 매일 자정 실행)
	void updateExpiredCoupons() throws Exception;

	// 특정 회원에게 쿠폰 발급
	void insertMemberCoupon(MemberCoupon memberCoupon) throws Exception;

	// 특정 회원 보유 쿠폰 목록 조회 (쿠폰 정보 포함)
	List<MemberCoupon> selectMemberCouponsByMemberNo(@Param("memberNo") Long memberNo) throws Exception;

	// 특정 회원 보유 쿠폰 수
	int countMemberCouponsByMemberNo(@Param("memberNo") Long memberNo) throws Exception;

	// 회원 보유 쿠폰 삭제
	void deleteMemberCoupon(@Param("memberCouponNo") Long memberCouponNo) throws Exception;

	// 회원 보유 쿠폰 만료 처리 (used_yn=Y, used_at=현재시각)
	void expireMemberCoupon(@Param("memberCouponNo") Long memberCouponNo) throws Exception;
}