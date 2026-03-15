package com.shop.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Product {
	 private Long productNo;
	 private String name;
	 private Long price;
	 private Long salePrice;
	 private Long categoryId;
	 private String description;
	 private String useYn;
	 private LocalDateTime createdAt;
	 private LocalDateTime updatedAt;
	 private Integer viewCount;
	 private String sameDayDeliveryYn;
	 
}