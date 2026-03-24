package com.shop.mapper.user;

import java.util.List;
import com.shop.domain.Faq;
import com.shop.dto.user.inquiry.FaqCreateRequest;
import com.shop.dto.user.inquiry.FaqPageRequest;
import org.apache.ibatis.annotations.Mapper;

// FAQ DB 접근 Mapper
// 조회는 사용자/관리자 공통 사용, 등록/삭제는 관리자만 호출
@Mapper
public interface FaqMapper {

    // FAQ 전체 조회 (삭제되지 않은 것만)
    List<Faq> readAllFaq() throws Exception;

    // 카테고리별 FAQ 조회
    List<Faq> readFaqByCategory(String category) throws Exception;

    // FAQ 키워드 검색 (질문 또는 답변에 키워드 포함)
    List<Faq> searchFaq(String keyword) throws Exception;

    // FAQ 페이징 조회 (카테고리 + 키워드 필터 포함)
    List<Faq> getFaqPage(FaqPageRequest request) throws Exception;

    // FAQ 전체 건수 조회 (페이징 계산용)
    int countFaq(FaqPageRequest request) throws Exception;

    // FAQ 등록 (관리자 전용 - AdminFaqController에서 호출)
    void createFaq(FaqCreateRequest request) throws Exception;

    // FAQ 삭제 (관리자 전용 - soft delete)
    void deleteFaq(Long faqNo) throws Exception;
}