package com.shop.mapper.user;

import java.util.List;
import com.shop.domain.InquiryFile;
import org.apache.ibatis.annotations.Mapper;

// 1:1 문의 첨부파일 DB 접근 Mapper
@Mapper
public interface InquiryFileMapper {

    // 첨부파일 저장
    void insertFile(InquiryFile inquiryFile) throws Exception;

    // 문의글에 해당하는 첨부파일 목록 조회 (삭제되지 않은 것만)
    List<InquiryFile> getFilesByInquiryNo(Long inquiryNo) throws Exception;

    // 첨부파일 삭제 (soft delete)
    void deleteFile(Long fileNo) throws Exception;
}