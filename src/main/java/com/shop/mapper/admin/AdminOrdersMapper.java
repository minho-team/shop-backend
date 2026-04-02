package com.shop.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Orders;
import com.shop.dto.admin.member.AdminOrderSummaryDTO;

// 관리자 회원 상세 페이지용 주문 조회 Mapper
// AdminMemberServiceImpl에서 사용
@Mapper
public interface AdminOrdersMapper {

    // 특정 회원 최근 주문 5건 조회 (회원 상세 요약용) - Orders 도메인 그대로 사용
    List<Orders> selectRecentOrdersByMemberNo(Long memberNo);

    // 특정 회원 주문 전체 페이징 조회 - 상품명 포함을 위해 AdminOrderSummaryDTO 사용
    // Orders 도메인을 수정하지 않고 독립 DTO로 분리하여 기존 코드 영향 없음
    List<AdminOrderSummaryDTO> selectOrderPageByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow);

    // 특정 회원 주문 전체 건수 (페이징 계산용)
    int countOrdersByMemberNo(Long memberNo);
}	