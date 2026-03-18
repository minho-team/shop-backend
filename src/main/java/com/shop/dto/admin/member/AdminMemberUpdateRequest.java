package com.shop.dto.admin.member;

import lombok.Data;

// ================================================
// 관리자 회원 정보 수정 요청 DTO
// 관리자가 수정 가능한 항목만 포함
// (아이디, 비밀번호, 가입일 등 핵심 보안 정보는 수정 불가)
// ================================================
@Data
public class AdminMemberUpdateRequest {

    // 이름
    private String name;

    // 닉네임
    private String nickName;

    // 이메일
    private String email;

    // 전화번호
    private String phoneNumber;

    // 우편번호
    private String zipCode;

    // 기본 주소
    private String basicAddress;

    // 상세 주소
    private String detailAddress;

    // 은행명
    private String bankName;

    // 계좌번호
    private String bankCode;

    // 예금주
    private String accountHolderName;
}