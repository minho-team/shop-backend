package com.shop.dto.admin.refund;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRefundPageResponseDTO {
    private List<AdminRefundListResponseDTO> list;
    private int page;
    private int size;
    private int totalCount;
    private int totalPage;
    private int startPage;
    private int endPage;
    private boolean hasPrev;
    private boolean hasNext;
}
