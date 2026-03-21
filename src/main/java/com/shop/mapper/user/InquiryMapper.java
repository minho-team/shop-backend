package com.shop.mapper.user;

import com.shop.domain.Inquiry;
import com.shop.dto.user.inquiry.InquiryCreateRequest;
import com.shop.dto.user.inquiry.InquiryPageRequest;
import com.shop.dto.user.inquiry.UpdateInquiryRequest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InquiryMapper {

    // 게시글 작성
    void createInquiry(InquiryCreateRequest request) throws Exception;

    // 전체 문의 목록 조회 (관리자용)
    List<Inquiry> readAllInquiry() throws Exception;

    // 내 문의 목록 조회
    List<Inquiry> readMyInquiry(Long memberNo) throws Exception;

    // 문의 단건 조회
    Inquiry readOneInquiry(Long inquiryNo) throws Exception;

    // 조회수 1 증가
    void increaseViewCount(Long inquiryNo) throws Exception;

    // 문의 수정
    void updateInquiry(Long inquiryNo, UpdateInquiryRequest dto) throws Exception;

    // 문의 삭제 (soft delete, 본인만)
    void deleteInquiry(Long inquiryNo, Long memberNo) throws Exception;

    // 문의 삭제 (관리자 전용, soft delete)
    void adminDeleteInquiry(Long inquiryNo) throws Exception;

    // 문의 상태 변경
    void updateStatus(Long inquiryNo, String status) throws Exception;

    // 전체 문의 페이징 조회 (관리자용)
    List<Inquiry> getInquiryPage(InquiryPageRequest request) throws Exception;

    // 전체 문의 건수 조회 (관리자 페이징 계산용)
    int countInquiry(InquiryPageRequest request) throws Exception;

    // 내 문의 페이징 조회 (사용자용)
    List<Inquiry> getMyInquiryPage(InquiryPageRequest request) throws Exception;

    // 내 문의 건수 조회 (사용자 페이징 계산용)
    int countMyInquiry(Long memberNo) throws Exception;

    // 관리자 - 특정 회원 최근 문의 3건 조회 (회원 상세 요약용)
    List<Inquiry> selectRecentInquiriesByMemberNo(Long memberNo) throws Exception;

    // ================================================
    // 관리자 - 특정 회원 문의 전체 페이징 조회 (5개씩)
    // memberNo: 조회할 회원 번호
    // startRow, endRow: Oracle 페이징 행 번호
    // ================================================
    List<Inquiry> selectInquiryPageByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow) throws Exception;

    // ================================================
    // 관리자 - 특정 회원 문의 전체 건수 조회 (페이징 계산용)
    // ================================================
    int countInquiryByMemberNo(Long memberNo) throws Exception;
}