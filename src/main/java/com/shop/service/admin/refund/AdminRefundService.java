package com.shop.service.admin.refund;

import com.shop.dto.admin.refund.AdminRefundDetailResponseDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundPageResponseDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateResponseDTO;

public interface AdminRefundService {

	AdminRefundPageResponseDTO getRefundList(AdminRefundListRequestDTO request);

    AdminRefundDetailResponseDTO getRefundDetail(Long refundNo);

    AdminRefundStatusUpdateResponseDTO updateRefundStatus(
            Long refundNo,
            AdminRefundStatusUpdateRequestDTO request
    );
    
    public void decideRefund(Long refundNo, String status) throws Exception;
    
}
