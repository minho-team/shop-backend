package com.shop.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.shop.mapper.admin.AdminMemberMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;

// 사용자 회원 관련 API 컨트롤러
// 관리자 회원 관리는 AdminMemberController에 있음
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AdminMemberMapper adminMemberMapper;

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