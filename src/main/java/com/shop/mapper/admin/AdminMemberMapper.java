package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.Member;
import com.shop.dto.admin.category.AdminCategoryListDTO;
import com.shop.dto.admin.member.AdminMemberSearchDTO;

@Mapper
public interface AdminMemberMapper {

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