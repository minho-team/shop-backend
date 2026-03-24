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

    // ================================================
    // 사용자 - 문의 기본 기능
    // ================================================

    // 문의 작성
    void createInquiry(InquiryCreateRequest request) throws Exception;

    // 전체 문의 목록 조회 (관리자용)
    List<Inquiry> readAllInquiry() throws Exception;

    // 내 문의 목록 조회 (사용자용)
    List<Inquiry> readMyInquiry(Long memberNo) throws Exception;

    // 문의 단건 조회
    Inquiry readOneInquiry(Long inquiryNo) throws Exception;

    // 조회수 1 증가
    void increaseViewCount(Long inquiryNo) throws Exception;

    // 문의 수정
    // [주의] 파라미터 2개 이상은 반드시 @Param 필요 - 없으면 MyBatis가 #{inquiryNo}, #{dto} 를 못 찾음
    void updateInquiry(@Param("inquiryNo") Long inquiryNo, @Param("dto") UpdateInquiryRequest dto) throws Exception;

    // 문의 삭제 (soft delete, 본인만)
    // [주의] 파라미터 2개 이상은 반드시 @Param 필요
    void deleteInquiry(@Param("inquiryNo") Long inquiryNo, @Param("memberNo") Long memberNo) throws Exception;

    // 문의 삭제 (관리자 전용, soft delete)
    void adminDeleteInquiry(Long inquiryNo) throws Exception;

    // 문의 상태 변경 (PENDING → ANSWERED 등)
    // [수정] @Param 누락으로 MyBatis가 #{inquiryNo}, #{status} 바인딩 실패 → "답변 등록 실패" 원인
    // CommentServiceImpl.createComment() / deleteComment() 에서 호출
    void updateStatus(@Param("inquiryNo") Long inquiryNo, @Param("status") String status) throws Exception;

    // ================================================
    // 페이징 조회
    // ================================================

    // 전체 문의 페이징 조회 (관리자용)
    List<Inquiry> getInquiryPage(InquiryPageRequest request) throws Exception;

    // 전체 문의 건수 조회 (관리자 페이징 계산용)
    int countInquiry(InquiryPageRequest request) throws Exception;

    // 내 문의 페이징 조회 (사용자용)
    List<Inquiry> getMyInquiryPage(InquiryPageRequest request) throws Exception;

    // 내 문의 건수 조회 (사용자 페이징 계산용)
    int countMyInquiry(Long memberNo) throws Exception;

    // ================================================
    // 관리자 - 회원 상세 페이지용
    // [추가 이유] AdminMemberServiceImpl.getMemberDetail()에서
    // 회원 상세 조회 시 최근 문의 요약 정보가 필요하여 추가
    // ================================================

    // 관리자 - 특정 회원 최근 문의 3건 조회 (회원 상세 요약용)
    List<Inquiry> selectRecentInquiriesByMemberNo(Long memberNo) throws Exception;

    // 관리자 - 특정 회원 문의 전체 페이징 조회
    // [추가 이유] AdminMemberDetailPage에서 문의 목록 페이징이 필요하여 추가
    List<Inquiry> selectInquiryPageByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow) throws Exception;

    // 관리자 - 특정 회원 문의 전체 건수 (페이징 계산용)
    // [추가 이유] selectInquiryPageByMemberNo 페이징 처리를 위한 총 건수 조회용
    int countInquiryByMemberNo(Long memberNo) throws Exception;
}