package com.shop.service.admin.member;

import java.util.List;
import java.util.Map;

import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.Member;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
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

    // 특정 회원 장바구니 목록 조회
    List<AdminCartItemDTO> getMemberCartItems(Long memberNo) throws Exception;

    // 메모 목록 조회
    List<MemberMemo> getMemberMemoList(Long memberNo) throws Exception;

    // 메모 등록
    void addMemberMemo(MemberMemo memo) throws Exception;

    // 메모 삭제
    void deleteMemberMemo(Long memoNo) throws Exception;

    // 회원 보유 쿠폰 목록 조회
    List<Map<String, Object>> getMemberCouponList(Long memberNo) throws Exception;

    // 전체 쿠폰 마스터 목록 조회
    List<Coupon> getCouponMasterList() throws Exception;

    // 회원에게 쿠폰 발급
    void issueCoupon(Long memberNo, Long couponNo, int validDays) throws Exception;
    
 // 쿠폰 마스터 생성
    void createCoupon(Coupon coupon) throws Exception;
}