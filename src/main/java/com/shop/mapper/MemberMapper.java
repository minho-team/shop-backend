package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.dto.admin.member.AdminMemberSearchDTO;

public interface MemberMapper {

	// 회원 + 역할 목록 함께 조회 (JWT 인증용)
	Member readOneMemberWithRoles(String memberId) throws Exception;

	// memberId로 회원 한 명 조회
	Member readOneMember(String memberId) throws Exception;

	// 로그인 시 리프레시 토큰 업데이트
	void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	// 회원가입
	void register(Member member) throws Exception;

	// 회원 역할 추가
	void insertRole(MemberRole role) throws Exception;

	// 관리자 계정 등록
	void insertAdmin(Member member) throws Exception;

	// 관리자 역할 추가
	void insertAdminRole(MemberRole mr) throws Exception;
	
	void insertKakaoMember(Member member) throws Exception;
	
	void insertRoleByMemberNo(@Param("memberNo") Long memberNo, @Param("roleName") String roleName) throws Exception;
	

	// ================================================
	// 관리자 - 전체 회원 수 조회 (페이징 계산용)
	// status, keyword 조건 포함
	// ================================================
	int selectMemberCount(AdminMemberSearchDTO dto) throws Exception;

	// ================================================
	// 관리자 - 회원 목록 페이징 조회
	// dto 안에 startRow, endRow, status, keyword 포함
	// ================================================
	List<Member> selectMemberList(AdminMemberSearchDTO dto) throws Exception;

	// ================================================
	// 관리자 - 회원 번호로 단건 상세 조회
	// ================================================
	Member selectMemberByNo(Long memberNo) throws Exception;

	// ================================================
	// 관리자 - 회원 상태 변경 (ACTIVE/DORMANT/SUSPENDED)
	// @Param으로 두 파라미터를 구분해서 XML에 전달
	// ================================================
	void updateMemberStatus(@Param("memberNo") Long memberNo,
	                        @Param("status") String status) throws Exception;

	// ================================================
	// 관리자 - 회원 정보 수정
	// Member 객체에 memberNo + 수정할 필드 담아서 전달
	// ================================================
	void updateMember(Member member) throws Exception;

	// ================================================
	// 관리자 - 회원 삭제 (하드 딜리트)
	// ================================================
	void deleteMember(Long memberNo) throws Exception;

}