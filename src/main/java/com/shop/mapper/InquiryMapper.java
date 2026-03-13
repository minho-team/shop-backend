package com.shop.mapper;

import java.util.List;

import com.shop.domain.Inquiry;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InquiryMapper {

    void createInquiry(InquiryCreateRequest request) throws Exception;

    List<Inquiry> readAllInquiry() throws Exception;

    List<Inquiry> readMyInquiry(Long memberNo) throws Exception;

    Inquiry readOneInquiry(Long inquiryNo) throws Exception;

    void increaseViewCount(Long inquiryNo) throws Exception;

    void updateInquiry(@Param("inquiryNo") Long inquiryNo,
                       @Param("dto") UpdateInquiryRequest dto) throws Exception;

    void deleteInquiry(@Param("inquiryNo") Long inquiryNo,
                       @Param("memberNo") Long memberNo) throws Exception;

    void updateStatus(@Param("inquiryNo") Long inquiryNo,
                      @Param("status") String status) throws Exception;
}