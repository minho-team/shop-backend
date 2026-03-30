package com.shop.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.wishlist.WishlistCheckResponseDTO;
import com.shop.dto.user.wishlist.WishlistItemResponseDTO;
import com.shop.service.user.member.MemberService;
import com.shop.service.user.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {
	private final WishlistService wishlistService;
	private final MemberService memberService;

	// 내 찜 목록 조회
	@GetMapping("/me")
	public ResponseEntity<?> getMyWishlist(Authentication authentication) {
		try {
			Member member = getAuthenticatedMember(authentication);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			List<WishlistItemResponseDTO> list = wishlistService.getMyWishlist(member.getMemberNo());
			return ResponseEntity.ok(list);

		} catch (Exception e) {
			log.error("찜 목록 조회 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 목록 조회에 실패했습니다.");
		}
	}

	// 해당 상품 찜 여부 확인
	@GetMapping("/check/{productNo}")
	public ResponseEntity<?> checkWishlist(@PathVariable Long productNo, Authentication authentication) {
		try {
			Member member = getAuthenticatedMember(authentication);

			if (member == null) {
				return ResponseEntity.ok(new WishlistCheckResponseDTO(false));
			}

			boolean wished = wishlistService.isWished(member.getMemberNo(), productNo);
			return ResponseEntity.ok(new WishlistCheckResponseDTO(wished));

		} catch (Exception e) {
			log.error("찜 여부 확인 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 여부 확인에 실패했습니다.");
		}
	}

	// 찜 추가
	@PostMapping("/{productNo}")
	public ResponseEntity<?> addWishlist(@PathVariable Long productNo, Authentication authentication) {
		try {
			Member member = getAuthenticatedMember(authentication);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			wishlistService.addWishlist(member.getMemberNo(), productNo);
			return ResponseEntity.ok(new WishlistCheckResponseDTO(true));

		} catch (Exception e) {
			log.error("찜 추가 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 추가에 실패했습니다.");
		}
	}

	// 찜 삭제
	@DeleteMapping("/{productNo}")
	public ResponseEntity<?> removeWishlist(@PathVariable Long productNo, Authentication authentication) {
		try {
			Member member = getAuthenticatedMember(authentication);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			wishlistService.removeWishlist(member.getMemberNo(), productNo);
			return ResponseEntity.ok(new WishlistCheckResponseDTO(false));

		} catch (Exception e) {
			log.error("찜 삭제 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 삭제에 실패했습니다.");
		}
	}

	private Member getAuthenticatedMember(Authentication authentication) throws Exception {
		if (authentication == null) {
			return null;
		}

		String memberId = authentication.getName();
		return memberService.readOneMember(memberId);
	}
}
