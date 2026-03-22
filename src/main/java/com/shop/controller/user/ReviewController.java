package com.shop.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Member;
import com.shop.domain.Review;
import com.shop.dto.user.review.MyReviewResponseDTO;
import com.shop.dto.user.review.ReviewSaveRequestDTO;
import com.shop.service.user.member.MemberService;
import com.shop.service.user.review.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final MemberService memberService;

	@PostMapping("/register")
	public ResponseEntity<?> registerReview(@ModelAttribute ReviewSaveRequestDTO dto,
			@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {
		log.info("리뷰 등록 시도 - 상품번호: {}, 주문항목번호: {}, 회원번호: {}", dto.getProductNo(), dto.getOrderItemNo(),
				dto.getMemberNo());

		try {
			Review review = new Review();
			review.setMemberNo(dto.getMemberNo());
			review.setProductNo(dto.getProductNo());
			review.setOrderItemNo(dto.getOrderItemNo());
			review.setTitle(dto.getTitle());
			review.setContent(dto.getContent());
			review.setRating(dto.getRating());
			review.setUserHeight(dto.getUserHeight());
			review.setUserWeight(dto.getUserWeight());
			review.setSizeRating(dto.getSizeRating());

			reviewService.registerReview(review, uploadFile);

			return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");

		} catch (Exception e) {
			log.error("리뷰 등록 중 예외 발생", e);
			return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
		}
	}

	@GetMapping("/product/{productNo}")
	public ResponseEntity<?> getProductReviews(@PathVariable Long productNo) {
		return ResponseEntity.ok(reviewService.getReviewListByProduct(productNo));
	}

	@GetMapping("/check/{orderItemNo}")
	public ResponseEntity<Boolean> checkReviewed(@PathVariable Long orderItemNo) {
		boolean isReviewed = reviewService.checkAlreadyReviewed(orderItemNo);
		return ResponseEntity.ok(isReviewed);
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMyReviews(Authentication authentication) {
		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			// 1. 로그인한 사용자 아이디 추출
			String memberId = authentication.getName();

			// 2. 회원 아이디로 회원 번호(memberNo) 조회
			Member member = memberService.readOneMember(memberId);
			if (member == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
			}

			// 3. 해당 회원의 리뷰 목록 조회 후 반환
			List<MyReviewResponseDTO> myReviews = reviewService.getMyReviews(member.getMemberNo());
			return ResponseEntity.ok(myReviews);

		} catch (Exception e) {
			log.error("나의 리뷰 조회 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 발생: " + e.getMessage());
		}
	}
}