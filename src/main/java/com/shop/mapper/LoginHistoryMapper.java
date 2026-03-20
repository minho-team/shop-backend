package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.LoginHistory;

public interface LoginHistoryMapper {

	// ================================================
	// 로그인 이력 저장
	// 로그인 성공 시 호출 (로그인 구현 시 연동 예정)
	// ================================================
	void insertLoginHistory(LoginHistory history) throws Exception;

	// ================================================
	// 관리자 - 특정 회원 로그인 이력 페이징 조회 (최신순)
	// ================================================
	List<LoginHistory> selectLoginHistoryByMemberNo(@Param("memberNo") Long memberNo, @Param("startRow") int startRow,
			@Param("endRow") int endRow) throws Exception;

	// ================================================
	// 관리자 - 특정 회원 로그인 이력 총 건수
	// ================================================
	int countLoginHistoryByMemberNo(@Param("memberNo") Long memberNo) throws Exception;
}