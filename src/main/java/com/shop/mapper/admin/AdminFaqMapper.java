package com.shop.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import com.shop.dto.user.inquiry.FaqCreateRequest;

// 관리자 FAQ 등록/삭제 전용 Mapper
// 조회는 FaqMapper(user)에서 처리
@Mapper
public interface AdminFaqMapper {

    // FAQ 등록 (관리자 전용)
    void createFaq(FaqCreateRequest request) throws Exception;

    // FAQ 삭제 (관리자 전용, soft delete)
    void deleteFaq(Long faqNo) throws Exception;

    // sortOrder >= fromOrder 인 항목 +1 이동
    void incrementSortOrderFrom(Integer fromOrder) throws Exception;

    // 전체 활성 FAQ sortOrder 재번호 (1, 2, 3...)
    void reorderFaqSortOrder() throws Exception;
}