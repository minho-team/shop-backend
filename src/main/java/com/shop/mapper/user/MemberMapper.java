package com.shop.mapper.user;

import org.apache.ibatis.annotations.Param;
import com.shop.domain.Member;
import com.shop.domain.MemberRole;

public interface MemberMapper {

    // 회원 + 역할 목록 함께 조회 (JWT 인증용)
    Member readOneMemberWithRoles(String memberId) throws Exception;

    // memberId로 회원 한 명 조회
    Member readOneMember(String memberId) throws Exception;

    // 로그인 시 리프레시 토큰 업데이트
    void updateRefreshToken(@Param("memberId") String memberId, @Param("refreshToken") String refreshToken) throws Exception;

    // 회원가입
    void register(Member member) throws Exception;

    // 회원 역할 추가
    void insertRole(MemberRole role) throws Exception;

    // 관리자 계정 등록
    void insertAdmin(Member member) throws Exception;

    // 관리자 역할 추가
    void insertAdminRole(MemberRole mr) throws Exception;
    
    // 카카오 회원 등록
    void insertKakaoMember(Member member) throws Exception;
    
    // 회원 번호 기반 역할 추가
    void insertRoleByMemberNo(@Param("memberNo") Long memberNo, @Param("roleName") String roleName) throws Exception;
    
    // 구매 횟수 1 증가 - @Param 추가로 XML과 확실한 연결
    void incrementPurchaseCount(@Param("memberNo") Long memberNo);

    // 구매 횟수 1 감소 - @Param 추가
    void decrementPurchaseCount(@Param("memberNo") Long memberNo);

    // 현재 구매 횟수 조회
    int getPurchaseCount(@Param("memberNo") Long memberNo);

    // 환불 신청 시, 사용자의 은행명, 계좌번호 업데이트 
	void updateBankInfo(Long memberNo, String bankName, String bankCode);
}