package com.shop.service.admin.member;

import java.util.List;
import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.LoginHistory;
import com.shop.domain.Member;
import com.shop.domain.MemberCoupon;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
import com.shop.domain.Point;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.admin.member.AdminMemberDetailResponse;
import com.shop.dto.admin.member.AdminMemberSearchDTO;
import com.shop.dto.admin.member.AdminMemberUpdateRequest;
import com.shop.dto.user.inquiry.PageResponse;

public interface AdminMemberService {

	// 회원 목록 페이징 조회
	PageResponse<Member> getMemberList(AdminMemberSearchDTO dto) throws Exception;

	// 회원 상세 조회
	AdminMemberDetailResponse getMemberDetail(Long memberNo) throws Exception;

	// 회원 상태 변경
	void updateMemberStatus(Long memberNo, String status) throws Exception;

	// 회원 정보 수정
	void updateMember(Long memberNo, AdminMemberUpdateRequest request) throws Exception;

	// 회원 삭제
	void deleteMember(Long memberNo) throws Exception;

	// 특정 회원 주문 목록 페이징 조회
	PageResponse<Orders> getMemberOrderPage(Long memberNo, int page, int size) throws Exception;

	// 특정 회원 문의 목록 페이징 조회
	PageResponse<Inquiry> getMemberInquiryPage(Long memberNo, int page, int size) throws Exception;

	// 특정 회원 장바구니 상품 목록 조회
	List<AdminCartItemDTO> getMemberCartItems(Long memberNo) throws Exception;

	// 특정 회원 로그인 이력 페이징 조회
	PageResponse<LoginHistory> getLoginHistoryPage(Long memberNo, int page, int size) throws Exception;

	// 메모 저장
	void insertMemo(Long memberNo, String content) throws Exception;

	// 특정 회원 메모 전체 조회
	List<MemberMemo> getMemosByMemberNo(Long memberNo) throws Exception;

	// 메모 삭제
	void deleteMemo(Long memoNo) throws Exception;

	// 포인트 지급/차감
	void insertPoint(Long memberNo, int point, String type) throws Exception;

	// 특정 회원 포인트 잔액 조회
	int getPointBalance(Long memberNo) throws Exception;

	// 특정 회원 포인트 이력 페이징 조회
	PageResponse<Point> getPointPage(Long memberNo, int page, int size) throws Exception;

	// 쿠폰 생성
	void insertCoupon(Coupon coupon) throws Exception;

	// 쿠폰 전체 목록 조회
	List<Coupon> getAllCoupons() throws Exception;

	// 쿠폰 소프트 삭제 여부 변경 (N=정상, Y=삭제)
	void updateCouponDeleteYn(Long couponNo, String deleteYn) throws Exception;

	// 쿠폰 삭제 (발급된 쿠폰 없을 때만 가능)
	void deleteCoupon(Long couponNo) throws Exception;

	// 특정 회원에게 쿠폰 발급 (startAt/endAt 발급 시 지정)
	void issueCouponToMember(Long memberNo, Long couponNo, String startAt, String endAt) throws Exception;

	// 특정 회원 보유 쿠폰 목록 조회
	List<MemberCoupon> getMemberCoupons(Long memberNo) throws Exception;

	// 회원 보유 쿠폰 삭제
	void deleteMemberCoupon(Long memberCouponNo) throws Exception;

	// 회원 보유 쿠폰 만료 처리
	void expireMemberCoupon(Long memberCouponNo) throws Exception;
}