package com.shop.service.user.review;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Review;
import com.shop.dto.user.review.MyReviewResponseDTO;
import com.shop.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewMapper reviewMapper;

	@Value("${upload.path}") // application.properties의 C:/upload 경로 사용
	private String uploadPath;

	@Override
	public void registerReview(Review review, MultipartFile file) throws Exception {

		// 1. 파일이 있는 경우 파일 저장 로직 실행
		if (file != null && !file.isEmpty()) {
			String originalName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String fileName = uuid + "_" + originalName;

			File saveFile = new File(uploadPath, fileName);
			file.transferTo(saveFile); // 물리적 파일 저장

			review.setImageUrl(fileName); // DB에는 저장된 파일명(또는 경로) 기록
		}

		// 2. DB에 리뷰 정보 저장
		reviewMapper.insertReview(review);
	}

	@Override
	public List<Review> getReviewListByProduct(int productNo) {
		return reviewMapper.getReviewListByProduct(productNo);
	}

	@Override
	public boolean checkAlreadyReviewed(int orderItemNo) {
		// order_item_no로 기존 리뷰를 조회하여 존재 여부 반환
		Review existingReview = reviewMapper.getOneReviewByOrderItem(orderItemNo);
		return existingReview != null;
	}
	
	@Override
	public List<MyReviewResponseDTO> getMyReviews(Long memberNo) {
	    return reviewMapper.selectReviewsByMemberNo(memberNo);
	}
}