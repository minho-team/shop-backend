package com.shop.domain;

import java.time.LocalDateTime;

import lombok.Data;

// 1:1 문의 게시판 테이블 데이터를 담는 도메인 클래스
@Data
public class Inquiry {

	// 게시글 번호 (PK)
	private Long inquiryNo;

	// 작성자 회원 번호 (FK → member.member_no)
	private Long memberNo;

	// 작성자 회원 아이디 (JOIN으로 가져옴 - member.member_id)
	private String memberId;

	// 작성자 회원 이름 (JOIN으로 가져옴 - member.name)
	private String memberName;

	// 문의 카테고리 (배송 / 주문/결제 / 취소/교환/반품 / 상품/AS문의 / 회원정보 / 서비스 / 이용안내)
	private String category;

	// 게시글 제목
	private String title;

	// 게시글 내용
	private String content;

	// 비밀글 여부 (Y: 비밀글 / N: 공개글)
	private String secretYn;

	// 문의 상태 (답변대기 / 답변완료)
	private String status;

	// 조회수
	private Long viewCount;

	// 작성일
	private LocalDateTime createdAt;

	// 수정일
	private LocalDateTime updatedAt;

	// 삭제 여부 (N: 정상 / Y: 삭제)
	private String deleteYn;

}
