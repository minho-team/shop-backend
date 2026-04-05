package com.shop.service.admin.refund;

import com.shop.dto.admin.refund.AdminRefundDetailResponseDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundPageResponseDTO;

public interface AdminRefundService {

	AdminRefundPageResponseDTO getRefundList(AdminRefundListRequestDTO request);

    AdminRefundDetailResponseDTO getRefundDetail(Long refundNo);
    
    void decideRefund(Long memberNo, Long refundNo, String status) throws Exception;
    
}
