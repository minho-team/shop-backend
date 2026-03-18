package com.shop.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.category.AdminCategoryListDTO;
import com.shop.service.admin.category.AdminCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;

	@GetMapping
	public ResponseEntity<?> getCategoryList() {
		try {
			List<AdminCategoryListDTO> list = adminCategoryService.getCategoryList();
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("카테고리 목록 조회 중 오류가 발생했습니다.");
		}
	}
}