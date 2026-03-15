package com.shop.dto.admin.product;

import java.util.List;

import lombok.Data;

// 페이지 응답 DTO
@Data
public class AdminProductPageResponseDTO {
	private List<AdminProductListDTO> list;

    private int page;
    private int size;

    private int totalCount;
    private int totalPage;
}
