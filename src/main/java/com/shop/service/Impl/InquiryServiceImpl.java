package com.shop.service.Impl;

import com.shop.domain.Inquiry;
import com.shop.domain.InquiryFile;
import com.shop.domain.Comment;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import com.shop.mapper.InquiryFileMapper;
import com.shop.mapper.InquiryMapper;
import com.shop.mapper.CommentMapper;
import com.shop.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 1:1 문의 게시판 서비스 구현 클래스
@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

	// Mapper 주입
	private final InquiryMapper inquiryMapper;
	private final InquiryFileMapper inquiryFileMapper;
	private final CommentMapper commentMapper;

	// application.properties의 업로드 경로 (기본값: C:/upload)
	@Value("${upload.path:C:/upload}")
	private String uploadPath;

	// 게시글 작성 처리 (첨부파일 포함)
	@Override
	public ResponseEntity<?> createInquiry(InquiryCreateRequest request, List<MultipartFile> files) {
		try {
			// 1. 게시글 저장 후 생성된 inquiry_no 반환받기
			inquiryMapper.createInquiry(request);

			// 2. 첨부파일이 있으면 서버에 저장하고 DB에 경로 기록
			if (files != null && !files.isEmpty()) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						saveFile(file, request.getMemberNo());
					}
				}
			}

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("게시글 작성 실패: " + e.getMessage());
		}
	}

	// 파일 저장 처리 - 서버 폴더에 저장 후 DB에 경로 기록
	private void saveFile(MultipartFile file, Long memberNo) throws Exception {
		// 업로드 폴더 없으면 자동 생성
		File uploadDir = new File(uploadPath + "/inquiry");
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		// UUID로 파일명 중복 방지
		String originalName = file.getOriginalFilename();
		String savedName = UUID.randomUUID().toString() + "_" + originalName;
		String filePath = uploadPath + "/inquiry/" + savedName;

		// 실제 파일 저장
		file.transferTo(new File(filePath));

		// DB에 파일 정보 INSERT
		InquiryFile inquiryFile = new InquiryFile();
		inquiryFile.setFileUrl("/upload/inquiry/" + savedName); // 접근 URL
		inquiryFile.setFileName(originalName);
		inquiryFile.setFileSize(file.getSize());
		inquiryFile.setFileType(file.getContentType());
		inquiryFileMapper.insertFile(inquiryFile);
	}

	// 게시글 전체 조회 처리 (관리자용)
	@Override
	public ResponseEntity<?> readAllInquiry() {
		try {
			List<Inquiry> list = inquiryMapper.readAllInquiry();
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("게시글 조회 실패: " + e.getMessage());
		}
	}

	// 내 문의 내역 조회 처리
	@Override
	public ResponseEntity<?> readMyInquiry(Long memberNo) {
		try {
			List<Inquiry> list = inquiryMapper.readMyInquiry(memberNo);
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("문의 내역 조회 실패: " + e.getMessage());
		}
	}

	// 게시글 하나 조회 처리 (첨부파일 + 댓글 포함)
	@Override
	public ResponseEntity<?> readOneInquiry(Long inquiryNo) {
		try {
			// 게시글 조회
			Inquiry inquiry = inquiryMapper.readOneInquiry(inquiryNo);
			if (inquiry == null)
				return ResponseEntity.notFound().build();

			// 조회수 증가
			inquiryMapper.increaseViewCount(inquiryNo);

			// 첨부파일 목록 조회
			List<InquiryFile> files = inquiryFileMapper.getFilesByInquiryNo(inquiryNo);

			// 관리자 답변 목록 조회
			List<Comment> comments = commentMapper.getCommentsByInquiryNo(inquiryNo);

			// 게시글 + 첨부파일 + 댓글 묶어서 반환
			Map<String, Object> result = new HashMap<>();
			result.put("inquiry", inquiry);
			result.put("files", files);
			result.put("comments", comments);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("게시글 조회 실패: " + e.getMessage());
		}
	}

	// 게시글 수정 처리
	@Override
	public ResponseEntity<?> updateInquiry(Long inquiryNo, UpdateInquiryRequest dto) {
		try {
			inquiryMapper.updateInquiry(inquiryNo, dto);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("게시글 수정 실패: " + e.getMessage());
		}
	}

	// 게시글 삭제 처리 (soft delete, 본인 글만 가능)
	@Override
	public ResponseEntity<?> deleteInquiry(Long inquiryNo, Long memberNo) {
		try {
			inquiryMapper.deleteInquiry(inquiryNo, memberNo);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("게시글 삭제 실패: " + e.getMessage());
		}
	}
}
