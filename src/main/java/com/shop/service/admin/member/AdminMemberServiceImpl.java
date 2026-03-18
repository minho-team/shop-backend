package com.shop.service.admin.member;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.Inquiry;
import com.shop.domain.Member;
import com.shop.domain.Orders;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.admin.member.AdminMemberDetailResponse;
import com.shop.dto.admin.member.AdminMemberSearchDTO;
import com.shop.dto.admin.member.AdminMemberUpdateRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.mapper.CartItemMapper;
import com.shop.mapper.InquiryMapper;
import com.shop.mapper.MemberMapper;
import com.shop.mapper.OrdersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberMapper    memberMapper;
    private final OrdersMapper    ordersMapper;
    private final InquiryMapper   inquiryMapper;
    private final CartItemMapper  cartItemMapper;

    // ================================================
    // 회원 목록 페이징 조회
    // ================================================
    @Override
    public PageResponse<Member> getMemberList(AdminMemberSearchDTO dto) throws Exception {
        dto.setStartRow((dto.getPage() - 1) * dto.getSize() + 1);
        dto.setEndRow(dto.getPage() * dto.getSize());
        int totalCount = memberMapper.selectMemberCount(dto);
        List<Member> list = memberMapper.selectMemberList(dto);
        return new PageResponse<>(list, totalCount, dto.getPage(), dto.getSize());
    }

    // ================================================
    // 회원 상세 조회
    // 기본정보 + 최근 주문 5건 + 최근 문의 3건 + 장바구니 수량
    // ================================================
    @Override
    public AdminMemberDetailResponse getMemberDetail(Long memberNo) throws Exception {
        Member member = memberMapper.selectMemberByNo(memberNo);
        if (member == null) throw new Exception("존재하지 않는 회원입니다. memberNo=" + memberNo);

        List<Orders>  recentOrders    = ordersMapper.selectRecentOrdersByMemberNo(memberNo);
        List<Inquiry> recentInquiries = inquiryMapper.selectRecentInquiriesByMemberNo(memberNo);
        int           cartItemCount   = cartItemMapper.countCartItemByMemberNo(memberNo);

        return new AdminMemberDetailResponse(member, recentOrders, recentInquiries, cartItemCount);
    }

    // ================================================
    // 회원 상태 변경
    // ================================================
    @Override
    public void updateMemberStatus(Long memberNo, String status) throws Exception {
        memberMapper.updateMemberStatus(memberNo, status);
    }

    // ================================================
    // 회원 정보 수정
    // ================================================
    @Override
    public void updateMember(Long memberNo, AdminMemberUpdateRequest request) throws Exception {
        Member member = new Member();
        member.setMemberNo(memberNo);
        member.setName(request.getName());
        member.setNickName(request.getNickName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setZipCode(request.getZipCode());
        member.setBasicAddress(request.getBasicAddress());
        member.setDetailAddress(request.getDetailAddress());
        member.setBankName(request.getBankName());
        member.setBankCode(request.getBankCode());
        member.setAccountHolderName(request.getAccountHolderName());
        memberMapper.updateMember(member);
    }

    // ================================================
    // 회원 삭제
    // ================================================
    @Override
    public void deleteMember(Long memberNo) throws Exception {
        memberMapper.deleteMember(memberNo);
    }

    // ================================================
    // 특정 회원 주문 목록 페이징 조회 (5개씩)
    // ================================================
    @Override
    public PageResponse<Orders> getMemberOrderPage(Long memberNo, int page, int size) throws Exception {
        int startRow   = (page - 1) * size + 1;
        int endRow     = page * size;
        int totalCount = ordersMapper.countOrdersByMemberNo(memberNo);
        List<Orders> list = ordersMapper.selectOrderPageByMemberNo(memberNo, startRow, endRow);
        return new PageResponse<>(list, totalCount, page, size);
    }

    // ================================================
    // 특정 회원 문의 목록 페이징 조회 (5개씩)
    // ================================================
    @Override
    public PageResponse<Inquiry> getMemberInquiryPage(Long memberNo, int page, int size) throws Exception {
        int startRow   = (page - 1) * size + 1;
        int endRow     = page * size;
        int totalCount = inquiryMapper.countInquiryByMemberNo(memberNo);
        List<Inquiry> list = inquiryMapper.selectInquiryPageByMemberNo(memberNo, startRow, endRow);
        return new PageResponse<>(list, totalCount, page, size);
    }

    // ================================================
    // 특정 회원 장바구니 상품 목록 조회
    // ================================================
    @Override
    public List<AdminCartItemDTO> getMemberCartItems(Long memberNo) throws Exception {
        return cartItemMapper.selectCartItemsWithProductByMemberNo(memberNo);
    }

}