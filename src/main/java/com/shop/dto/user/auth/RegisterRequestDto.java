package com.shop.dto.user.auth;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequestDto {

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

	private String bankName;
	private String bankCode;
	private String accountHolderName;
}
