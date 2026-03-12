package com.shop.domain;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class Product {
	 private Long productNo;
	 private String name;
	 private int price;
	 private int salePrice;
	 private Long categoryId;
	 private String description;
	 private String useYn;
	 private LocalDateTime createdAt;
	 private LocalDateTime updatedAt;
	 private int viewCount;
	 private String sameDayDeliveryYn;
	 
}