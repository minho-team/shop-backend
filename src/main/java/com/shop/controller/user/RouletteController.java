package com.shop.controller.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.service.user.member.MemberService;
import com.shop.service.user.roulette.RouletteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roulette")
@RequiredArgsConstructor
public class RouletteController {

    private final RouletteService rouletteService;
    private final MemberService memberService;

    // 룰렛 돌리기 (로그인 필요)
    // POST /api/roulette/spin
    @PostMapping("/spin")
    public ResponseEntity<?> spin(Authentication authentication) {
        try {
            Long memberNo = memberService.readOneMember(authentication.getName()).getMemberNo();
            Map<String, Object> result = rouletteService.spin(memberNo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}