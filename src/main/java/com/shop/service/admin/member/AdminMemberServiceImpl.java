package com.shop.service.admin.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.Member;
import com.shop.domain.MemberCoupon;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.admin.member.AdminMemberDetailResponse;
import com.shop.dto.admin.member.AdminMemberSearchDTO;
import com.shop.dto.admin.member.AdminMemberUpdateRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.mapper.admin.AdminCartItemMapper;
import com.shop.mapper.admin.AdminInquiryMapper;
import com.shop.mapper.admin.AdminMemberMapper;
import com.shop.mapper.admin.AdminOrdersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;
    // ★ 분리: OrdersMapper → AdminOrdersMapper
    private final AdminOrdersMapper adminOrdersMapper;
    // ★ 분리: CartItemMapper → AdminCartItemMapper
    private final AdminCartItemMapper adminCartItemMapper;
    // ★ 분리: InquiryMapper → AdminInquiryMapper
    private final AdminInquiryMapper adminInquiryMapper;

    // ================================================
    // 1. 회원 기본 관리
    // ================================================

    // 회원 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<Member> getMemberList(AdminMemberSearchDTO dto) throws Exception {
        dto.setStartRow((dto.getPage() - 1) * dto.getSize() + 1);
        dto.setEndRow(dto.getPage() * dto.getSize());
        int totalCount = adminMemberMapper.selectMemberCount(dto);
        List<Member> list = adminMemberMapper.selectMemberList(dto);
        return new PageResponse<>(list, totalCount, dto.getPage(), dto.getSize());
    }

    // 회원 상세 조회 (기본정보 + 최근주문 + 최근문의 + 장바구니수)
    @Override
    @Transactional(readOnly = true)
    public AdminMemberDetailResponse getMemberDetail(Long memberNo) throws Exception {
        Member member = adminMemberMapper.selectMemberByNo(memberNo);
        if (member == null) {
            throw new Exception("존재하지 않는 회원입니다. memberNo=" + memberNo);
        }
        List<Orders> recentOrders = adminOrdersMapper.selectRecentOrdersByMemberNo(memberNo);
        List<Inquiry> recentInquiries = adminInquiryMapper.selectRecentInquiriesByMemberNo(memberNo);
        int cartItemCount = adminCartItemMapper.countCartItemByMemberNo(memberNo);
        return new AdminMemberDetailResponse(member, recentOrders, recentInquiries, cartItemCount);
    }

    // 회원 상태 변경
    @Override
    public void updateMemberStatus(Long memberNo, String status) throws Exception {
        adminMemberMapper.updateMemberStatus(memberNo, status);
    }

    // 회원 정보 수정
    @Override
    public void updateMember(Long memberNo, AdminMemberUpdateRequest request) throws Exception {
        Member member = new Member();
        member.setMemberNo(memberNo);
        member.setName(request.getName());
        member.setNickName(request.getNickName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setGender(request.getGender());
        member.setBirthday(request.getBirthday());
        member.setZipCode(request.getZipCode());
        member.setBasicAddress(request.getBasicAddress());
        member.setDetailAddress(request.getDetailAddress());
        member.setBankName(request.getBankName());
        member.setBankCode(request.getBankCode());
        member.setAccountHolderName(request.getAccountHolderName());
        adminMemberMapper.updateMember(member);
    }

    // 회원 삭제
    @Override
    public void deleteMember(Long memberNo) throws Exception {
        adminMemberMapper.deleteMember(memberNo);
    }

    // ================================================
    // 2. 활동 내역 조회
    // ================================================

    // 특정 회원 주문 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<Orders> getMemberOrderPage(Long memberNo, int page, int size) throws Exception {
        int startRow = (page - 1) * size;
        int endRow = page * size;
        int totalCount = adminOrdersMapper.countOrdersByMemberNo(memberNo);
        List<Orders> list = adminOrdersMapper.selectOrderPageByMemberNo(memberNo, startRow, endRow);
        return new PageResponse<>(list, totalCount, page, size);
    }

    // 특정 회원 문의 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<Inquiry> getMemberInquiryPage(Long memberNo, int page, int size) throws Exception {
        int startRow = (page - 1) * size;
        int endRow = page * size;
        int totalCount = adminInquiryMapper.countInquiryByMemberNo(memberNo);
        List<Inquiry> list = adminInquiryMapper.selectInquiryPageByMemberNo(memberNo, startRow, endRow);
        return new PageResponse<>(list, totalCount, page, size);
    }

    // 특정 회원 장바구니 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminCartItemDTO> getMemberCartItems(Long memberNo) throws Exception {
        return adminCartItemMapper.selectCartItemsWithProductByMemberNo(memberNo);
    }

    // ================================================
    // 3. 메모 관리
    // ================================================

    // 메모 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MemberMemo> getMemberMemoList(Long memberNo) throws Exception {
        return adminMemberMapper.selectMemberMemoList(memberNo);
    }

    // 메모 등록
    @Override
    public void addMemberMemo(MemberMemo memo) throws Exception {
        adminMemberMapper.insertMemberMemo(memo);
    }

    // 메모 삭제
    @Override
    public void deleteMemberMemo(Long memoNo) throws Exception {
        adminMemberMapper.deleteMemberMemo(memoNo);
    }

    // ================================================
    // 4. 쿠폰 관리
    // ================================================

    // 회원 보유 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMemberCouponList(Long memberNo) throws Exception {
        return adminMemberMapper.selectMemberCouponList(memberNo);
    }

    // 발급 가능한 전체 쿠폰 마스터 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<Coupon> getCouponMasterList() throws Exception {
        return adminMemberMapper.selectAllCoupons();
    }

    // 회원에게 쿠폰 발급
    @Override
    public void issueCoupon(Long memberNo, Long couponNo, int validDays) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        MemberCoupon mc = MemberCoupon.builder()
                .memberNo(memberNo)
                .couponNo(couponNo)
                .usedYn("N")
                .issuedAt(now)
                .startAt(now)
                .endAt(now.plusDays(validDays))
                .build();
        adminMemberMapper.insertMemberCoupon(mc);
    }

    // 쿠폰 마스터 생성
    @Override
    public void createCoupon(Coupon coupon) throws Exception {
        adminMemberMapper.insertCoupon(coupon);
    }
}