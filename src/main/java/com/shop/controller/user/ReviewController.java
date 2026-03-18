package com.shop.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Review;
import com.shop.dto.user.review.ReviewSaveRequestDTO;
import com.shop.service.user.review.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/register")
	public ResponseEntity<?> registerReview(@ModelAttribute ReviewSaveRequestDTO dto, // @RequestBody 제거!
			@RequestParam(value = "uploadFile", required = false) org.springframework.web.multipart.MultipartFile uploadFile,
			org.springframework.security.core.Authentication authentication // 로그인 정보 가져오기
	) {
		log.info("리뷰 등록 요청 DTO: {}", dto);
		log.info("첨부 파일 존재 여부: {}", uploadFile != null);

		try {
			// 1. 로그인한 사용자의 memberNo를 세션/토큰에서 직접 가져오는 것이 안전합니다.
			// 만약 리액트에서 memberNo를 안 보냈다면 여기서 채워줘야 합니다.
			// String memberId = authentication.getName();
			// Member member = memberService.readOneMember(memberId);
			// review.setMemberNo(member.getMemberNo());

			Review review = new Review();
			review.setMemberNo(dto.getMemberNo()); // 리액트에서 보냈는지 확인 필요
			review.setProductNo(dto.getProductNo());
			review.setOrderItemNo(dto.getOrderItemNo());
			review.setTitle(dto.getTitle());
			review.setContent(dto.getContent());
			review.setRating(dto.getRating());
			review.setUserHeight(dto.getUserHeight());
			review.setUserWeight(dto.getUserWeight());
			review.setSizeRating(dto.getSizeRating());

			// 2. 서비스로 파일과 함께 전달 (서비스에서 파일 저장 로직 수행)
			reviewService.registerReview(review, uploadFile);

			return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");

		} catch (Exception e) {
			log.error("리뷰 등록 중 예외 발생", e);
			return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
		}
	}

	@GetMapping("/product/{productNo}")
	public ResponseEntity<?> getProductReviews(@PathVariable int productNo) {
		return ResponseEntity.ok(reviewService.getReviewListByProduct(productNo));
	}

	@GetMapping("/check/{orderItemNo}")
	public ResponseEntity<Boolean> checkReviewed(@PathVariable int orderItemNo) {
		boolean isReviewed = reviewService.checkAlreadyReviewed(orderItemNo);
		return ResponseEntity.ok(isReviewed);
	}
}