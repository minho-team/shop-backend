package com.shop.dto.admin.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponseDto {

    private int currentPage;
    private int size;
    private int totalCount;
    private int totalPage;

    private int startPage;
    private int endPage;

    private boolean hasPrev;
    private boolean hasNext;
}
