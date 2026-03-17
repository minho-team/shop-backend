package com.shop.mapper;

import com.shop.domain.Inquiry;
import com.shop.dto.user.inquiry.InquiryCreateRequest;
import com.shop.dto.user.inquiry.InquiryPageRequest;
import com.shop.dto.user.inquiry.UpdateInquiryRequest;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

// 1:1 문의 DB 접근을 담당하는 MyBatis Mapper
@Mapper
public interface InquiryMapper {

    // 게시글 작성
    void createInquiry(InquiryCreateRequest request) throws Exception;

    // 전체 문의 목록 조회 (관리자용)
    List<Inquiry> readAllInquiry() throws Exception;

    // 내 문의 목록 조회 (로그인한 회원)
    List<Inquiry> readMyInquiry(Long memberNo) throws Exception;

    // 문의 단건 조회
    Inquiry readOneInquiry(Long inquiryNo) throws Exception;

    // 조회수 1 증가 - 게시글 상세 조회 시 호출
    void increaseViewCount(Long inquiryNo) throws Exception;

    // 문의 수정 - 카테고리, 제목, 내용, 비밀글 여부 변경
    void updateInquiry(Long inquiryNo, UpdateInquiryRequest dto) throws Exception;

    // 문의 삭제 (soft delete) - delete_yn을 Y로 변경, 본인 글만 삭제 가능
    void deleteInquiry(Long inquiryNo, Long memberNo) throws Exception;

    // =========================================
    // 문의 삭제 (관리자 전용, soft delete)
    // memberNo 체크 없이 모든 문의 삭제 가능
    // =========================================
    void adminDeleteInquiry(Long inquiryNo) throws Exception;

    // 문의 상태 변경 - 답변 등록/삭제 시 자동 업데이트 (답변대기 / 답변완료)
    void updateStatus(Long inquiryNo, String status) throws Exception;

    // =========================================
    // 전체 문의 페이징 조회 (관리자용 - 상태/카테고리/키워드 필터 포함)
    // request: page, size, status, category, keyword, startRow, endRow
    // =========================================
    List<Inquiry> getInquiryPage(InquiryPageRequest request) throws Exception;

    // =========================================
    // 전체 문의 건수 조회 (관리자 페이징 계산용)
    // request: status, category, keyword 필터 적용
    // =========================================
    int countInquiry(InquiryPageRequest request) throws Exception;

    // =========================================
    // 내 문의 페이징 조회 (로그인 회원 - memberNo 기준)
    // request: page, size, memberNo, startRow, endRow
    // =========================================
    List<Inquiry> getMyInquiryPage(InquiryPageRequest request) throws Exception;

    // =========================================
    // 내 문의 건수 조회 (사용자 페이징 계산용)
    // memberNo: 로그인한 회원 번호
    // =========================================
    int countMyInquiry(Long memberNo) throws Exception;
}
