package com.shop.dto.user.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageResponseDto {
    private int currentPage;
    private int size;
    private int totalCount;
    private int totalPage;
    private int startPage;
    private int endPage;
    private boolean hasPrev;
    private boolean hasNext;

    public PageResponseDto(int totalCount, int page, int size) {
        this.totalCount = totalCount;
        this.currentPage = page;
        this.size = size;

        this.totalPage = (int) Math.ceil(totalCount / (double) size);
        this.endPage = (int) (Math.ceil(page / 5.0)) * 5;
        this.startPage = this.endPage - 4;

        if (this.totalPage < this.endPage) {
            this.endPage = this.totalPage;
        }

        this.hasPrev = this.startPage > 1;
        this.hasNext = this.endPage < this.totalPage;
    }
}