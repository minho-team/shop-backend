package com.shop.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Product {
	 private int product_no;
	 private String name;
	 private int price;
	 private int sale_price;
	 private int category_id;
	 private String description;
	 private String use_yn;
	 private Date created_at;
	 private Date updated_at;
	 private int view_count;
	 private String same_day_delivery_yn;
	 
}