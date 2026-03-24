package com.shop.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.shop.dto.admin.member.AdminCartItemDTO;

// 관리자 장바구니 조회 전용 Mapper
// AdminMemberServiceImpl.getMemberDetail()에서 사용
@Mapper
public interface AdminCartItemMapper {

    // 특정 회원 장바구니 담긴 상품 종류 수 조회 (회원 상세 요약용)
    int countCartItemByMemberNo(Long memberNo) throws Exception;

    // 특정 회원 장바구니 상품 목록 상세 조회
    // 상품명, 옵션(사이즈/색상), 수량, 가격 포함
    List<AdminCartItemDTO> selectCartItemsWithProductByMemberNo(Long memberNo) throws Exception;
}