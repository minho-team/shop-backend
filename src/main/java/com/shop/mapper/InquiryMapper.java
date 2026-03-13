package com.shop.mapper;

import java.util.List;
import com.shop.domain.Inquiry;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 1:1 문의 게시판 MyBatis 매퍼 인터페이스
@Mapper
public interface InquiryMapper {

    // 문의 게시글 작성 (시퀀스로 inquiry_no 채번 후 INSERT)
    void createInquiry(InquiryCreateRequest request) throws Exception;

    // 전체 문의 목록 조회 (관리자용 - 삭제되지 않은 글만, 최신순 정렬)
    List<Inquiry> readAllInquiry() throws Exception;

    // 내 문의 목록 조회 (로그인한 회원의 글만, 최신순 정렬)
    // memberNo: 로그인한 회원 번호
    List<Inquiry> readMyInquiry(Long memberNo) throws Exception;

    // 문의 단건 조회 (삭제되지 않은 글만)
    // inquiryNo: 조회할 문의 번호
    Inquiry readOneInquiry(Long inquiryNo) throws Exception;

    // 조회수 1 증가 (게시글 상세 조회 시 호출)
    // inquiryNo: 조회수를 증가시킬 문의 번호
    void increaseViewCount(Long inquiryNo) throws Exception;

    // 문의 수정 (카테고리, 제목, 내용, 비밀글 여부 변경)
    // inquiryNo: 수정할 문의 번호 / dto: 수정할 데이터
    void updateInquiry(@Param("inquiryNo") Long inquiryNo,
                       @Param("dto") UpdateInquiryRequest dto) throws Exception;

    // 문의 삭제 (soft delete - delete_yn = Y, 본인 글만 가능)
    // inquiryNo: 삭제할 문의 번호 / memberNo: 본인 확인용 회원 번호
    void deleteInquiry(@Param("inquiryNo") Long inquiryNo,
                       @Param("memberNo") Long memberNo) throws Exception;

    // 문의 상태 변경 (답변 등록/삭제 시 자동 업데이트)
    // inquiryNo: 상태를 변경할 문의 번호 / status: 변경할 상태 값 (답변대기 / 답변완료)
    void updateStatus(@Param("inquiryNo") Long inquiryNo,
                      @Param("status") String status) throws Exception;
}