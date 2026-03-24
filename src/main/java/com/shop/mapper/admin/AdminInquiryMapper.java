package com.shop.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Inquiry;
import com.shop.dto.user.inquiry.InquiryPageRequest;

// 관리자 전용 문의 DB 접근 Mapper
// 사용자 문의 기능은 InquiryMapper(user)에서 처리
@Mapper
public interface AdminInquiryMapper {

    // 전체 문의 목록 조회
    List<Inquiry> readAllInquiry();
    
    // 전체 문의 페이징 조회
    List<Inquiry> getInquiryPage(InquiryPageRequest request);
    
    // 전체 문의 건수 조회 (페이징 계산용)
    int countInquiry(InquiryPageRequest request);
    
    // 문의 삭제 (관리자 전용, soft delete)
    void adminDeleteInquiry(Long inquiryNo);
    
    // 문의 상태 변경
    void updateStatus(@Param("inquiryNo") Long inquiryNo, @Param("status") String status);
    
    // 특정 회원 최근 문의 3건 조회 (회원 상세 요약용)
    List<Inquiry> selectRecentInquiriesByMemberNo(Long memberNo);
    
    // 특정 회원 문의 전체 페이징 조회 (회원 상세 페이지용)
    List<Inquiry> selectInquiryPageByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow);
    
    // 특정 회원 문의 전체 건수 (페이징 계산용)
    int countInquiryByMemberNo(Long memberNo);
}