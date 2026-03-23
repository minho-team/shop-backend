package com.shop.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.service.admin.product.AdminProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductImageController {

    private final AdminProductImageService adminProductImageService;

    // 상품 이미지 조회
    @GetMapping("/{productNo}/images")
    public ResponseEntity<?> getProductImages(@PathVariable Long productNo) {
        try {
            return ResponseEntity.ok(adminProductImageService.getProductImages(productNo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 이미지 조회 중 오류가 발생했습니다.");
        }
    }

    // 썸네일 이미지 변경
    @PutMapping("/{productNo}/images/thumb")
    public ResponseEntity<?> updateThumbImage(
            @PathVariable Long productNo,
            @RequestParam("file") MultipartFile file) {
        try {
            adminProductImageService.updateThumbImage(productNo, file);
            return ResponseEntity.ok("썸네일 이미지가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("썸네일 이미지 변경 중 오류가 발생했습니다.");
        }
    }

    // 메인 이미지 변경
    @PutMapping("/{productNo}/images/main")
    public ResponseEntity<?> updateMainImage(
            @PathVariable Long productNo,
            @RequestParam("file") MultipartFile file) {
        try {
            adminProductImageService.updateMainImage(productNo, file);
            return ResponseEntity.ok("메인 이미지가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("메인 이미지 변경 중 오류가 발생했습니다.");
        }
    }

    // 메인 이미지 삭제
    @DeleteMapping("/{productNo}/images/main")
    public ResponseEntity<?> deleteMainImage(@PathVariable Long productNo) {
        try {
            adminProductImageService.deleteMainImage(productNo);
            return ResponseEntity.ok("메인 이미지가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("메인 이미지 삭제 중 오류가 발생했습니다.");
        }
    }

    // 갤러리 이미지 추가
    @PostMapping("/{productNo}/images/gallery")
    public ResponseEntity<?> addGalleryImage(
            @PathVariable Long productNo,
            @RequestParam("file") MultipartFile file) {
        try {
            adminProductImageService.addGalleryImage(productNo, file);
            return ResponseEntity.ok("갤러리 이미지가 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("갤러리 이미지 추가 중 오류가 발생했습니다.");
        }
    }

    // 갤러리 이미지 변경
    @PutMapping("/{productNo}/images/gallery/{productImgNo}")
    public ResponseEntity<?> updateGalleryImage(
            @PathVariable Long productNo,
            @PathVariable Long productImgNo,
            @RequestParam("file") MultipartFile file) {
        try {
            adminProductImageService.updateGalleryImage(productNo, productImgNo, file);
            return ResponseEntity.ok("갤러리 이미지가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("갤러리 이미지 변경 중 오류가 발생했습니다.");
        }
    }

    // 갤러리 이미지 삭제
    @DeleteMapping("/{productNo}/images/gallery/{productImgNo}")
    public ResponseEntity<?> deleteGalleryImage(
            @PathVariable Long productNo,
            @PathVariable Long productImgNo) {
        try {
            adminProductImageService.deleteGalleryImage(productNo, productImgNo);
            return ResponseEntity.ok("갤러리 이미지가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("갤러리 이미지 삭제 중 오류가 발생했습니다.");
        }
    }

    // 사이즈표 이미지 변경
    @PutMapping("/{productNo}/images/size")
    public ResponseEntity<?> updateSizeImage(
            @PathVariable Long productNo,
            @RequestParam("file") MultipartFile file) {
        try {
            adminProductImageService.updateSizeImage(productNo, file);
            return ResponseEntity.ok("사이즈표 이미지가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사이즈표 이미지 변경 중 오류가 발생했습니다.");
        }
    }

    // 사이즈표 이미지 삭제
    @DeleteMapping("/{productNo}/images/size")
    public ResponseEntity<?> deleteSizeImage(@PathVariable Long productNo) {
        try {
            adminProductImageService.deleteSizeImage(productNo);
            return ResponseEntity.ok("사이즈표 이미지가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사이즈표 이미지 삭제 중 오류가 발생했습니다.");
        }
    }
}