package com.shop.dto.admin.order;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderListResponse {

    private List<AdminOrderDto> content;
    private PageResponseDto pageInfo;
}
