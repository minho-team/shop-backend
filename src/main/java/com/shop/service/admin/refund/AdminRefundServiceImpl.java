package com.shop.service.admin.refund;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.admin.refund.AdminRefundDetailFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundDetailItemDTO;
import com.shop.dto.admin.refund.AdminRefundDetailResponseDTO;
import com.shop.dto.admin.refund.AdminRefundFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundListItemDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundListResponseDTO;
import com.shop.dto.admin.refund.AdminRefundPageResponseDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateResponseDTO;
import com.shop.mapper.admin.AdminRefundMapper;
import com.shop.service.user.member.MemberService;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

    private final AdminRefundMapper adminRefundMapper;
    // 환불 완료 시 purchaseCount 차감 및 grade 갱신을 위해 주입
    private final MemberService memberService;

    //환불 리스트 받아오기, 검색어, 페이징 포함
    @Override
    public AdminRefundPageResponseDTO getRefundList(AdminRefundListRequestDTO request) {
        int totalCount = adminRefundMapper.getRefundCount(request);

        int totalPage = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / request.getSize());

        int currentPage = request.getPage();
        if (totalPage > 0 && currentPage > totalPage) {
            currentPage = totalPage;
            request.setPage(currentPage);
        }

        List<AdminRefundFlatRowDTO> flatList = adminRefundMapper.getRefundList(request);

        Map<Long, AdminRefundListResponseDTO> groupedMap = new LinkedHashMap<>();

        for (AdminRefundFlatRowDTO row : flatList) {
            AdminRefundListResponseDTO refundDto = groupedMap.get(row.getRefundNo());

            if (refundDto == null) {
                refundDto = new AdminRefundListResponseDTO();
                refundDto.setRefundNo(row.getRefundNo());
                refundDto.setOrderNo(row.getOrderNo());
                refundDto.setRefundStatus(row.getRefundStatus());
                refundDto.setRequestedAt(row.getRequestedAt());
                refundDto.setMemberId(row.getMemberId());
                refundDto.setName(row.getName());
                refundDto.setTotalRefundAmount(row.getTotalRefundAmount());
                refundDto.setItems(new ArrayList<>());
                groupedMap.put(row.getRefundNo(), refundDto);
            }

            AdminRefundListItemDTO itemDto = new AdminRefundListItemDTO();
            itemDto.setRefundItemNo(row.getRefundItemNo());
            itemDto.setOrderItemNo(row.getOrderItemNo());
            itemDto.setItemName(row.getItemName());
            itemDto.setItemColor(row.getItemColor());
            itemDto.setItemSize(row.getItemSize());
            itemDto.setRefundQuantity(row.getRefundQuantity());
            itemDto.setRefundAmount(row.getRefundAmount());
            itemDto.setRefundItemStatus(row.getRefundItemStatus());

            refundDto.getItems().add(itemDto);
        }

        int blockSize = 5;
        int startPage = ((currentPage - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        return AdminRefundPageResponseDTO.builder()
                .list(new ArrayList<>(groupedMap.values()))
                .page(currentPage)
                .size(request.getSize())
                .totalCount(totalCount)
                .totalPage(totalPage)
                .startPage(totalPage == 0 ? 1 : startPage)
                .endPage(totalPage == 0 ? 1 : endPage)
                .hasPrev(startPage > 1)
                .hasNext(endPage < totalPage)
                .build();
    }

    @Override
    public AdminRefundDetailResponseDTO getRefundDetail(Long refundNo) {
        List<AdminRefundDetailFlatRowDTO> rows = adminRefundMapper.getRefundDetail(refundNo);

        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("환불 정보를 찾을 수 없습니다.");
        }

        AdminRefundDetailResponseDTO dto = new AdminRefundDetailResponseDTO();
        dto.setRefundNo(rows.get(0).getRefundNo());
        dto.setOrderNo(rows.get(0).getOrderNo());
        dto.setRefundStatus(rows.get(0).getRefundStatus());
        dto.setRequestedAt(rows.get(0).getRequestedAt());
        dto.setMemberId(rows.get(0).getMemberId());
        dto.setName(rows.get(0).getName());
        dto.setTotalRefundAmount(rows.get(0).getTotalRefundAmount());
        dto.setRefundReason(rows.get(0).getRefundReason());
        dto.setBankCode(rows.get(0).getBankCode());
        dto.setBankName(rows.get(0).getBankName());
        dto.setItems(new ArrayList<>());

        for (AdminRefundDetailFlatRowDTO row : rows) {
            AdminRefundDetailItemDTO itemDto = new AdminRefundDetailItemDTO();
            itemDto.setRefundItemNo(row.getRefundItemNo());
            itemDto.setOrderItemNo(row.getOrderItemNo());
            itemDto.setItemName(row.getItemName());
            itemDto.setItemColor(row.getItemColor());
            itemDto.setItemSize(row.getItemSize());
            itemDto.setRefundQuantity(row.getRefundQuantity());
            itemDto.setRefundAmount(row.getRefundAmount());
            itemDto.setRefundItemStatus(row.getRefundItemStatus());
            
            dto.getItems().add(itemDto);
        }

        return dto;
    }

    // 환불 상태 변경
    @Override
    @Transactional
    public AdminRefundStatusUpdateResponseDTO updateRefundStatus(
            Long refundNo,
            AdminRefundStatusUpdateRequestDTO request
    ) {
        String refundStatus = request.getRefundStatus();
 
        validateRefundStatus(refundStatus);
 
        int exists = adminRefundMapper.existsRefund(refundNo);
        if (exists == 0) {
            throw new IllegalArgumentException("환불 정보를 찾을 수 없습니다.");
        }
 
        // 상태별 refund / refund_item / order_item 동기화
        if ("REQUESTED".equals(refundStatus)) {
            adminRefundMapper.updateRefundItemsStatus(refundNo, "REQUESTED");
            adminRefundMapper.updateRefundHeaderStatus(refundNo, "REQUESTED");
            adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUND_REQUESTED");
 
        } else if ("APPROVED".equals(refundStatus)) {
            adminRefundMapper.updateRefundItemsStatus(refundNo, "APPROVED");
            adminRefundMapper.updateRefundHeaderStatus(refundNo, "APPROVED");
            adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUND_APPROVED");
 
        } else if ("REJECTED".equals(refundStatus)) {
            adminRefundMapper.updateRefundItemsStatus(refundNo, "REJECTED");
            adminRefundMapper.updateRefundHeaderStatus(refundNo, "REJECTED");
            adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REJECTED");
 
        } else if ("COMPLETED".equals(refundStatus)) {
            adminRefundMapper.updateRefundItemsStatus(refundNo, "COMPLETED");
            adminRefundMapper.updateRefundHeaderStatus(refundNo, "COMPLETED");
            adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUNDED");
 
            // ★ 환불 완료: 해당 환불건의 회원 memberNo 조회 후 purchaseCount -1, grade 재산정
            List<AdminRefundDetailFlatRowDTO> rows = adminRefundMapper.getRefundDetail(refundNo);
            if (rows != null && !rows.isEmpty()) {
                Long memberNo = rows.get(0).getMemberNo(); // ← 아래 DTO 수정 참고
                if (memberNo != null) {
                    try {
                        memberService.decreasePurchaseCount(memberNo);
                        memberService.updateMemberGrade(memberNo);
                        log.info("환불 완료 처리 - 회원 {}번 purchaseCount-1, grade 갱신 완료 (refundNo: {})",
                                memberNo, refundNo);
                    } catch (Exception e) {
                        log.error("환불 완료 후 purchaseCount 차감 실패 - refundNo: {}, 사유: {}",
                                refundNo, e.getMessage());
                        throw new RuntimeException("purchaseCount 차감 중 오류가 발생했습니다.", e);
                    }
                } else {
                    log.warn("환불 완료 처리 - memberNo 조회 실패 (refundNo: {})", refundNo);
                }
            }
        }
 
        // 최종 헤더 상태 확인 (모든 item 이 COMPLETED 면 헤더도 COMPLETED)
        String finalHeaderStatus;
        int notCompletedCount = adminRefundMapper.countNotCompletedRefundItems(refundNo);
        if (notCompletedCount == 0) {
            finalHeaderStatus = "COMPLETED";
            adminRefundMapper.updateRefundHeaderStatus(refundNo, "COMPLETED");
            adminRefundMapper.updateOrderItemsStatusByRefundNo(refundNo, "REFUNDED");
        } else {
            List<AdminRefundDetailFlatRowDTO> rows = adminRefundMapper.getRefundDetail(refundNo);
            finalHeaderStatus = rows.get(0).getRefundStatus();
        }
 
        return AdminRefundStatusUpdateResponseDTO.builder()
                .refundNo(refundNo)
                .refundStatus(finalHeaderStatus)
                .message("환불 상태가 변경되었습니다.")
                .build();
    }
 
    private void validateRefundStatus(String refundStatus) {
        if (!"REQUESTED".equals(refundStatus)
                && !"APPROVED".equals(refundStatus)
                && !"REJECTED".equals(refundStatus)
                && !"COMPLETED".equals(refundStatus)) {
            throw new IllegalArgumentException("허용되지 않는 환불 상태입니다.");
        }
    }
}