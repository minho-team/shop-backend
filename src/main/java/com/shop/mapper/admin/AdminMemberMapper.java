package com.shop.mapper.admin;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.Coupon;
import com.shop.domain.Member;
import com.shop.domain.MemberCoupon;
import com.shop.domain.MemberMemo;
import com.shop.dto.admin.member.AdminMemberSearchDTO;

@Mapper
public interface AdminMemberMapper {

	// ================================================
	// 1. 회원 기본 관리
	// ================================================
	// 회원 수 조회 (페이징용)
	int selectMemberCount(AdminMemberSearchDTO dto) throws Exception;

	// 회원 목록 조회 (페이징)
	List<Member> selectMemberList(AdminMemberSearchDTO dto) throws Exception;

	// 회원 단건 조회
	Member selectMemberByNo(Long memberNo) throws Exception;

	// 회원 상태 변경 (ACTIVE/DORMANT/SUSPENDED)
	void updateMemberStatus(@Param("memberNo") Long memberNo, @Param("status") String status) throws Exception;

	// 회원 정보 수정
	void updateMember(Member member) throws Exception;

	// 회원 삭제
	void deleteMember(Long memberNo) throws Exception;

	// ================================================
	// 2. 회원 메모 관리
	// ================================================
	// 특정 회원의 메모 목록 조회
	List<MemberMemo> selectMemberMemoList(Long memberNo) throws Exception;

	// 메모 등록
	void insertMemberMemo(MemberMemo memo) throws Exception;

	// 메모 삭제
	void deleteMemberMemo(Long memoNo) throws Exception;

	// ================================================
	// 3. 쿠폰 관리
	// ================================================
	// 회원 보유 쿠폰 목록 조회
	List<Map<String, Object>> selectMemberCouponList(Long memberNo) throws Exception;

	// 발급 가능한 전체 쿠폰 목록 조회
	List<Coupon> selectAllCoupons() throws Exception;

	// 회원에게 쿠폰 발급
	void insertMemberCoupon(MemberCoupon memberCoupon) throws Exception;

	// 쿠폰 마스터 생성
	void insertCoupon(Coupon coupon) throws Exception;

	// 쿠폰 마스터 삭제 (soft delete)
	void deleteCouponMaster(Long couponNo) throws Exception;

	// 회원 보유 쿠폰 삭제
	void deleteMemberCoupon(Long memberCouponNo) throws Exception;

	// 결제 시 쿠폰 유효성 검증 + 정보 조회 (미사용, 유효기간 내, 본인 쿠폰)
	Map<String, Object> selectMemberCouponForUse(@Param("memberCouponNo") Long memberCouponNo, @Param("memberNo") Long memberNo);

	// 쿠폰 사용 처리 (used_yn = 'Y', used_at = SYSTIMESTAMP)
	void updateMemberCouponUsed(@Param("memberCouponNo") Long memberCouponNo);

	// 쿠폰 사용 내역 조회 (관리자용 - 사용된 쿠폰 + 연결 주문)
	List<Map<String, Object>> selectCouponUsageHistory(Long memberNo) throws Exception;
}