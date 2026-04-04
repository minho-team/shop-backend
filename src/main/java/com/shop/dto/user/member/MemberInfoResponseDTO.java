package com.shop.dto.user.member;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MemberInfoResponseDTO {
	private Long memberNo;
	private String memberId;

	private String name;
	private String nickName;
	private String email;
	private String phoneNumber;

	private String zipCode;
	private String basicAddress;
	private String detailAddress;

	private String gender;
	private LocalDate birthday;
	private Long totalSpent;
	private String grade;
	
	// 일반 회원 / 카카오 회원 구분용
	private String provider;
}
