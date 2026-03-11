package com.shop.mapper;

import java.util.List;
import com.shop.domain.Faq;
import org.apache.ibatis.annotations.Mapper;

// FAQ DB 접근을 담당하는 MyBatis Mapper
@Mapper
public interface FaqMapper {

    // FAQ 전체 조회 (삭제되지 않은 것만, 카테고리·정렬순 기준)
    List<Faq> readAllFaq() throws Exception;

    // 카테고리별 FAQ 조회
    List<Faq> readFaqByCategory(String category) throws Exception;

    // FAQ 키워드 검색 (질문 또는 답변에 키워드 포함)
    List<Faq> searchFaq(String keyword) throws Exception;
}
