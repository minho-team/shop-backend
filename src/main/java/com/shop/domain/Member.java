package com.shop.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	private Long memberNo;
	private String memberId;
	private String password;
	private String name;
	private String nickName;
	private String email;
	private String phoneNumber;
	private String zipCode;
	private String basicAddress;
	private String detailAddress;
	private String gender;
	private LocalDate birthday;
	private String refreshToken;
	private LocalDateTime createdAt;
	private Integer purchaseCount;
	private String bankName;
	private String bankCode;
	private String accountHolderName;
	private String status;
	private String provider;

	// user_role 테이블 매핑용
	private List<MemberRole> memberRoleList;
}
