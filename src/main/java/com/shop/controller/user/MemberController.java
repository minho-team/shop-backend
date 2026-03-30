package com.shop.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.member.MemberInfoResponseDTO;
import com.shop.dto.user.member.MemberUpdateRequestDTO;
import com.shop.mapper.admin.AdminMemberMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 사용자 회원 관련 API 컨트롤러
// 관리자 회원 관리는 AdminMemberController에 있음
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

	private final MemberService memberService;
	private final AdminMemberMapper adminMemberMapper;

	// 내 정보 조회
	@GetMapping("/me")
	public ResponseEntity<?> getMyInfo(Authentication authentication) {
		try {
			if (authentication == null || authentication.getName() == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			String memberId = authentication.getName();
			Member member = memberService.readOneMember(memberId);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
			}

			MemberInfoResponseDTO dto = new MemberInfoResponseDTO();
			dto.setMemberNo(member.getMemberNo());
			dto.setMemberId(member.getMemberId());

			dto.setName(member.getName());
			dto.setNickName(member.getNickName());
			dto.setEmail(member.getEmail());
			dto.setPhoneNumber(member.getPhoneNumber());

			dto.setZipCode(member.getZipCode());
			dto.setBasicAddress(member.getBasicAddress());
			dto.setDetailAddress(member.getDetailAddress());

			dto.setGender(member.getGender());
			dto.setBirthday(member.getBirthday());

			dto.setProvider(member.getProvider());

			return ResponseEntity.ok(dto);

		} catch (Exception e) {
			log.error("내 정보 조회 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내 정보 조회 중 오류가 발생했습니다.");
		}
	}

	// 내 정보 수정
	@PutMapping("/me")
	public ResponseEntity<?> updateMyInfo(@RequestBody MemberUpdateRequestDTO request, Authentication authentication) {
		try {
			if (authentication == null || authentication.getName() == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			String memberId = authentication.getName();
			Member member = memberService.readOneMember(memberId);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
			}

			memberService.updateMemberInfo(member.getMemberNo(), request);
			return ResponseEntity.ok("회원 정보가 수정되었습니다.");

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());

		} catch (Exception e) {
			log.error("내 정보 수정 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정 중 오류가 발생했습니다.");
		}
	}

	// 내 쿠폰 목록 조회
	// GET /api/member/coupons
	// Oracle TIMESTAMP → TO_CHAR(String) 변환은 XML 쿼리에서 처리
	@GetMapping("/coupons")
	public ResponseEntity<?> getMyCoupons(Authentication authentication) {
		try {
			Long memberNo = memberService.readOneMember(authentication.getName()).getMemberNo();
			List<Map<String, Object>> coupons = adminMemberMapper.selectMemberCouponList(memberNo);
			return ResponseEntity.ok(coupons);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("쿠폰 조회 실패: " + e.getMessage());
		}
	}
}