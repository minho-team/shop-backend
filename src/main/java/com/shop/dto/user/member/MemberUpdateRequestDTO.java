package com.shop.dto.user.member;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MemberUpdateRequestDTO {
	private String name;
	private String nickName;
	private String email;
	private String phoneNumber;

	// 성별
	private String gender;

	// 생년월일 
	private LocalDate birthday;

	// 주소 정보
	private String zipCode;
	private String basicAddress;
	private String detailAddress;

	// 비밀번호 변경
	private String currentPassword;
	private String newPassword;
}
