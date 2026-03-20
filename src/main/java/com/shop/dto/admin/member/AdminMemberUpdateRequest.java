package com.shop.dto.admin.member;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AdminMemberUpdateRequest {

	// 기본 정보
	private String name;
	private String nickName;
	private String email;
	private String phoneNumber;

	// [추가] 성별 (M / F / null)
	private String gender;

	// [추가] 생년월일 (프론트에서 "YYYY-MM-DD" 문자열로 전송 → LocalDate 자동 변환)
	private LocalDate birthday;

	// 주소 정보
	private String zipCode;
	private String basicAddress;
	private String detailAddress;

	// 계좌 정보
	private String bankName;
	private String bankCode;
	private String accountHolderName;
}