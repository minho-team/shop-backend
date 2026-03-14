package com.shop.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Category {
	private Long categoryId;
	private String name;
	private Long parentId;
	private LocalDateTime createdAt;
	private String useYn;
}
