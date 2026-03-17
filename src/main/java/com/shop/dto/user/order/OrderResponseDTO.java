package com.shop.dto.user.order;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자
public class OrderResponseDTO {

	// 1. 타입을 OrderDTO로 통일합니다.
	private List<OrderDTO> orderList;
	private int startPage;
	private int endPage;
	private boolean prev, next;
	private int totalCount;
	private String memberName;

	// 2. 서비스에서 호출하는 바로 그 생성자입니다.
	// 첫 번째 인자가 반드시 List<OrderDTO>여야 에러가 사라집니다.
	public OrderResponseDTO(List<OrderDTO> orderList, int totalCount, int page, int size) {
		this.orderList = orderList;
		this.totalCount = totalCount;

		// 페이징 블록 계산 (5개씩 노출 기준)
		this.endPage = (int) (Math.ceil(page / 5.0)) * 5;
		this.startPage = this.endPage - 4;

		int realEnd = (int) (Math.ceil(totalCount / (double) size));

		if (realEnd < this.endPage) {
			this.endPage = realEnd;
		}

		this.prev = this.startPage > 1;
		this.next = this.endPage < realEnd;
	}
}