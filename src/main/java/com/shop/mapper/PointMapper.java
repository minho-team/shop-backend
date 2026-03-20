package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Point;

public interface PointMapper {

	// ================================================
	// 포인트 저장 (적립/차감/만료/관리자조정)
	// ================================================
	void insertPoint(Point point) throws Exception;

	// ================================================
	// 특정 회원 포인트 잔액 조회
	// SUM(point) 로 계산 (양수=적립 합산, 음수=차감 합산)
	// ================================================
	int selectPointBalance(@Param("memberNo") Long memberNo) throws Exception;

	// ================================================
	// 특정 회원 포인트 이력 페이징 조회 (최신순)
	// ================================================
	List<Point> selectPointsByMemberNo(@Param("memberNo") Long memberNo, @Param("startRow") int startRow,
			@Param("endRow") int endRow) throws Exception;

	// ================================================
	// 특정 회원 포인트 이력 총 건수
	// ================================================
	int countPointsByMemberNo(@Param("memberNo") Long memberNo) throws Exception;
}