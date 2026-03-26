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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

    private final AdminRefundMapper adminRefundMapper;

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
        }

        String finalHeaderStatus;
        int notCompletedCountAfter = adminRefundMapper.countNotCompletedRefundItems(refundNo);
        if (notCompletedCountAfter == 0) {
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