package com.shop.dto.admin.member;

import java.util.List;

import com.shop.domain.Inquiry;
import com.shop.domain.Member;
import com.shop.domain.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ================================================
// 관리자 회원 상세 조회 통합 응답 DTO
// 회원 기본 정보 + 최근 주문 + 최근 문의 + 장바구니 현황
// ================================================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberDetailResponse {

    // 회원 기본/주소/계좌/활동 정보
    private Member member;

    // 최근 주문 목록 (최대 5건, 최신순)
    private List<Orders> recentOrders;

    // 최근 1:1 문의 목록 (최대 3건, 최신순)
    private List<Inquiry> recentInquiries;

    // 장바구니에 담긴 상품 종류 수
    private int cartItemCount;
}