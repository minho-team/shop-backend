package com.shop.service.user.inquiry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Comment;
import com.shop.domain.Inquiry;
import com.shop.domain.InquiryFile;
import com.shop.dto.user.inquiry.InquiryCreateRequest;
import com.shop.dto.user.inquiry.InquiryPageRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.dto.user.inquiry.UpdateInquiryRequest;
import com.shop.mapper.admin.AdminInquiryMapper;
import com.shop.mapper.user.CommentMapper;
import com.shop.mapper.user.InquiryFileMapper;
import com.shop.mapper.user.InquiryMapper;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

  private final InquiryMapper inquiryMapper;
  private final AdminInquiryMapper adminInquiryMapper;
  private final InquiryFileMapper inquiryFileMapper;
  private final CommentMapper commentMapper;
  private final CustomFileUtil customFileUtil;

  @Override
  public ResponseEntity<?> createInquiry(InquiryCreateRequest request, List<MultipartFile> files) {
    try {
      inquiryMapper.createInquiry(request);

      if (files != null && !files.isEmpty()) {
        for (MultipartFile file : files) {
          if (file == null || file.isEmpty()) {
            continue;
          }

          String savedName = customFileUtil.saveFile(file);
          if (savedName == null) {
            continue;
          }

          InquiryFile inquiryFile = new InquiryFile();
          inquiryFile.setInquiryNo(request.getInquiryNo());
          inquiryFile.setFileUrl("/upload/" + savedName);
          inquiryFile.setFileName(file.getOriginalFilename());
          inquiryFile.setFileSize(file.getSize());
          inquiryFile.setFileType(file.getContentType());

          inquiryFileMapper.insertFile(inquiryFile);
        }
      }

      Map<String, Object> result = new HashMap<>();
      result.put("message", "문의가 등록되었습니다.");
      result.put("inquiryNo", request.getInquiryNo());
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 등록 실패: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> readAllInquiry() {
    try {
      List<Inquiry> list = adminInquiryMapper.readAllInquiry();
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 목록 조회 실패: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> readMyInquiry(Long memberNo) {
    try {
      List<Inquiry> list = inquiryMapper.readMyInquiry(memberNo);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("내 문의 조회 실패: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> readOneInquiry(Long inquiryNo) {
    try {
      inquiryMapper.increaseViewCount(inquiryNo);
      Inquiry inquiry = inquiryMapper.readOneInquiry(inquiryNo);

      if (inquiry == null) {
        return ResponseEntity.notFound().build();
      }

      List<InquiryFile> files = inquiryFileMapper.getFilesByInquiryNo(inquiryNo);
      List<Comment> comments = commentMapper.getCommentsByInquiryNo(inquiryNo);

      Map<String, Object> result = new HashMap<>();
      result.put("inquiry", inquiry);
      result.put("files", files);
      result.put("comments", comments);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 상세 조회 실패: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> updateInquiry(Long inquiryNo, UpdateInquiryRequest dto) {
    try {
      inquiryMapper.updateInquiry(inquiryNo, dto);
      return ResponseEntity.ok("문의가 수정되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 수정 실패: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> deleteInquiry(Long inquiryNo, Long memberNo) {
    try {
      inquiryMapper.deleteInquiry(inquiryNo, memberNo);
      return ResponseEntity.ok("문의가 삭제되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 삭제 실패: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> adminDeleteInquiry(Long inquiryNo) {
    try {
      adminInquiryMapper.adminDeleteInquiry(inquiryNo);
      return ResponseEntity.ok("문의가 삭제되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 삭제 실패: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> getInquiryPage(InquiryPageRequest request) {
    try {
      List<Inquiry> list = adminInquiryMapper.getInquiryPage(request);
      int totalCount = adminInquiryMapper.countInquiry(request);
      PageResponse<Inquiry> response = new PageResponse<>(list, totalCount, request.getPage(), request.getSize());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("문의 페이징 조회 실패: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> getMyInquiryPage(InquiryPageRequest request) {
    try {
      if (request.getMemberNo() == null) {
        return ResponseEntity.badRequest().body("회원번호가 필요합니다.");
      }

      List<Inquiry> list = inquiryMapper.getMyInquiryPage(request);
      int totalCount = inquiryMapper.countMyInquiry(request.getMemberNo());
      PageResponse<Inquiry> response = new PageResponse<>(list, totalCount, request.getPage(), request.getSize());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("내 문의 페이징 조회 실패: " + e.getMessage());
    }
  }
}