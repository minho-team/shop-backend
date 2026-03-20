package com.shop.service.admin.member;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.LoginHistory;
import com.shop.domain.Member;
import com.shop.domain.MemberCoupon;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
import com.shop.domain.Point;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.admin.member.AdminMemberDetailResponse;
import com.shop.dto.admin.member.AdminMemberSearchDTO;
import com.shop.dto.admin.member.AdminMemberUpdateRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.mapper.CartItemMapper;
import com.shop.mapper.CouponMapper;
import com.shop.mapper.InquiryMapper;
import com.shop.mapper.LoginHistoryMapper;
import com.shop.mapper.MemberMapper;
import com.shop.mapper.MemberMemoMapper;
import com.shop.mapper.OrdersMapper;
import com.shop.mapper.PointMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

	private final MemberMapper memberMapper;
	private final OrdersMapper ordersMapper;
	private final InquiryMapper inquiryMapper;
	private final CartItemMapper cartItemMapper;
	private final LoginHistoryMapper loginHistoryMapper;
	private final MemberMemoMapper memberMemoMapper;
	private final PointMapper pointMapper;
	private final CouponMapper couponMapper;

	// 회원 목록 페이징 조회
	@Override
	public PageResponse<Member> getMemberList(AdminMemberSearchDTO dto) throws Exception {
		dto.setStartRow((dto.getPage() - 1) * dto.getSize() + 1);
		dto.setEndRow(dto.getPage() * dto.getSize());
		int totalCount = memberMapper.selectMemberCount(dto);
		List<Member> list = memberMapper.selectMemberList(dto);
		return new PageResponse<>(list, totalCount, dto.getPage(), dto.getSize());
	}

	// 회원 상세 조회
	@Override
	public AdminMemberDetailResponse getMemberDetail(Long memberNo) throws Exception {
		Member member = memberMapper.selectMemberByNo(memberNo);
		if (member == null)
			throw new Exception("존재하지 않는 회원입니다. memberNo=" + memberNo);
		List<Orders> recentOrders = ordersMapper.selectRecentOrdersByMemberNo(memberNo);
		List<Inquiry> recentInquiries = inquiryMapper.selectRecentInquiriesByMemberNo(memberNo);
		int cartItemCount = cartItemMapper.countCartItemByMemberNo(memberNo);
		return new AdminMemberDetailResponse(member, recentOrders, recentInquiries, cartItemCount);
	}

	// 회원 상태 변경
	@Override
	public void updateMemberStatus(Long memberNo, String status) throws Exception {
		memberMapper.updateMemberStatus(memberNo, status);
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
		memberMapper.updateMember(member);
	}

	// 회원 삭제
	@Override
	public void deleteMember(Long memberNo) throws Exception {
		memberMapper.deleteMember(memberNo);
	}

	// 특정 회원 주문 목록 페이징 조회
	@Override
	public PageResponse<Orders> getMemberOrderPage(Long memberNo, int page, int size) throws Exception {
		int startRow = (page - 1) * size + 1;
		int endRow = page * size;
		int totalCount = ordersMapper.countOrdersByMemberNo(memberNo);
		List<Orders> list = ordersMapper.selectOrderPageByMemberNo(memberNo, startRow, endRow);
		return new PageResponse<>(list, totalCount, page, size);
	}

	// 특정 회원 문의 목록 페이징 조회
	@Override
	public PageResponse<Inquiry> getMemberInquiryPage(Long memberNo, int page, int size) throws Exception {
		int startRow = (page - 1) * size + 1;
		int endRow = page * size;
		int totalCount = inquiryMapper.countInquiryByMemberNo(memberNo);
		List<Inquiry> list = inquiryMapper.selectInquiryPageByMemberNo(memberNo, startRow, endRow);
		return new PageResponse<>(list, totalCount, page, size);
	}

	// 특정 회원 장바구니 상품 목록 조회
	@Override
	public List<AdminCartItemDTO> getMemberCartItems(Long memberNo) throws Exception {
		return cartItemMapper.selectCartItemsWithProductByMemberNo(memberNo);
	}

	// 특정 회원 로그인 이력 페이징 조회
	@Override
	public PageResponse<LoginHistory> getLoginHistoryPage(Long memberNo, int page, int size) throws Exception {
		int startRow = (page - 1) * size + 1;
		int endRow = page * size;
		int totalCount = loginHistoryMapper.countLoginHistoryByMemberNo(memberNo);
		List<LoginHistory> list = loginHistoryMapper.selectLoginHistoryByMemberNo(memberNo, startRow, endRow);
		return new PageResponse<>(list, totalCount, page, size);
	}

	// 메모 저장
	@Override
	public void insertMemo(Long memberNo, String content) throws Exception {
		MemberMemo memo = MemberMemo.builder().memberNo(memberNo).content(content).build();
		memberMemoMapper.insertMemo(memo);
	}

	// 특정 회원 메모 전체 조회
	@Override
	public List<MemberMemo> getMemosByMemberNo(Long memberNo) throws Exception {
		return memberMemoMapper.selectMemosByMemberNo(memberNo);
	}

	// 메모 삭제
	@Override
	public void deleteMemo(Long memoNo) throws Exception {
		memberMemoMapper.deleteMemo(memoNo);
	}

	// 포인트 지급/차감
	@Override
	public void insertPoint(Long memberNo, int point, String type) throws Exception {
		Point p = Point.builder().memberNo(memberNo).point(point).type(type).build();
		pointMapper.insertPoint(p);
	}

	// 특정 회원 포인트 잔액 조회
	@Override
	public int getPointBalance(Long memberNo) throws Exception {
		return pointMapper.selectPointBalance(memberNo);
	}

	// 특정 회원 포인트 이력 페이징 조회
	@Override
	public PageResponse<Point> getPointPage(Long memberNo, int page, int size) throws Exception {
		int startRow = (page - 1) * size + 1;
		int endRow = page * size;
		int totalCount = pointMapper.countPointsByMemberNo(memberNo);
		List<Point> list = pointMapper.selectPointsByMemberNo(memberNo, startRow, endRow);
		return new PageResponse<>(list, totalCount, page, size);
	}

	// 쿠폰 생성
	@Override
	public void insertCoupon(Coupon coupon) throws Exception {
		couponMapper.insertCoupon(coupon);
	}

	// 쿠폰 전체 목록 조회
	@Override
	public List<Coupon> getAllCoupons() throws Exception {
		return couponMapper.selectAllCoupons();
	}

	// 쿠폰 소프트 삭제 여부 변경 (N=정상, Y=삭제)
	@Override
	public void updateCouponDeleteYn(Long couponNo, String deleteYn) throws Exception {
		couponMapper.updateCouponDeleteYn(couponNo, deleteYn);
	}

	// 쿠폰 삭제 (발급된 쿠폰이 있으면 FK 제약으로 실패)
	@Override
	public void deleteCoupon(Long couponNo) throws Exception {
		couponMapper.deleteCoupon(couponNo);
	}

	// 특정 회원에게 쿠폰 발급 (startAt/endAt 발급 시 지정, ISO 문자열 → LocalDateTime 변환)
	@Override
	public void issueCouponToMember(Long memberNo, Long couponNo, String startAt, String endAt) throws Exception {
		MemberCoupon memberCoupon = MemberCoupon.builder().memberNo(memberNo).couponNo(couponNo)
				.startAt(startAt != null && !startAt.isEmpty() ? java.time.LocalDateTime.parse(startAt) : null)
				.endAt(endAt != null && !endAt.isEmpty() ? java.time.LocalDateTime.parse(endAt) : null).build();
		couponMapper.insertMemberCoupon(memberCoupon);
	}

	// 특정 회원 보유 쿠폰 목록 조회
	@Override
	public List<MemberCoupon> getMemberCoupons(Long memberNo) throws Exception {
		return couponMapper.selectMemberCouponsByMemberNo(memberNo);
	}

	// 회원 보유 쿠폰 삭제
	@Override
	public void deleteMemberCoupon(Long memberCouponNo) throws Exception {
		couponMapper.deleteMemberCoupon(memberCouponNo);
	}

	// 회원 보유 쿠폰 만료 처리 (used_yn=Y, used_at=현재시각)
	@Override
	public void expireMemberCoupon(Long memberCouponNo) throws Exception {
		couponMapper.expireMemberCoupon(memberCouponNo);
	}
}