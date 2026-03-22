package com.shop.service.admin.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.mapper.admin.AdminProductImageMapper;
import com.shop.mapper.admin.AdminProductMapper;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductImageServiceImpl implements AdminProductImageService {

    private final AdminProductMapper adminProductMapper;
    private final AdminProductImageMapper adminProductImageMapper;
    private final CustomFileUtil customFileUtil;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductImages(Long productNo) {
        validateProduct(productNo);

        AdminProductImageDTO thumbImage =
                adminProductImageMapper.getSingleImageByType(productNo, "THUMB");

        AdminProductImageDTO mainImage =
                adminProductImageMapper.getSingleImageByType(productNo, "MAIN");

        AdminProductImageDTO sizeImage =
                adminProductImageMapper.getSingleImageByType(productNo, "SIZE");

        List<AdminProductImageDTO> galleryImages =
                adminProductImageMapper.getGalleryImages(productNo);

        Map<String, Object> result = new HashMap<>();
        result.put("thumbnailImage", thumbImage);
        result.put("mainImage", mainImage);
        result.put("galleryImages", galleryImages);
        result.put("sizeChartImage", sizeImage);

        return result;
    }

    @Override
    @Transactional
    public void updateThumbImage(Long productNo, MultipartFile file) {
        validateProduct(productNo);
        validateFile(file);

        AdminProductImageDTO oldImage = adminProductImageMapper.getSingleImageByType(productNo, "THUMB");
        String savedFileName = customFileUtil.saveFile(file);

        if (savedFileName == null) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        if (oldImage == null) {
            AdminProductImageDTO dto = new AdminProductImageDTO();
            dto.setProductNo(productNo);
            dto.setImageUrl(savedFileName);
            dto.setImageType("THUMB");
            dto.setSortOrder(1);

            adminProductImageMapper.insertProductImage(dto);
        } else {
            deletePhysicalFile(oldImage.getImageUrl());

            oldImage.setImageUrl(savedFileName);
            adminProductImageMapper.updateImageUrl(oldImage);
        }
    }

    @Override
    @Transactional
    public void updateMainImage(Long productNo, MultipartFile file) {
        validateProduct(productNo);
        validateFile(file);

        AdminProductImageDTO oldImage = adminProductImageMapper.getSingleImageByType(productNo, "MAIN");
        String savedFileName = customFileUtil.saveFile(file);

        if (savedFileName == null) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        if (oldImage == null) {
            AdminProductImageDTO dto = new AdminProductImageDTO();
            dto.setProductNo(productNo);
            dto.setImageUrl(savedFileName);
            dto.setImageType("MAIN");
            dto.setSortOrder(1);

            adminProductImageMapper.insertProductImage(dto);
        } else {
            deletePhysicalFile(oldImage.getImageUrl());

            oldImage.setImageUrl(savedFileName);
            adminProductImageMapper.updateImageUrl(oldImage);
        }
    }

    @Override
    @Transactional
    public void deleteMainImage(Long productNo) {
        validateProduct(productNo);

        AdminProductImageDTO oldImage = adminProductImageMapper.getSingleImageByType(productNo, "MAIN");
        if (oldImage == null) {
            throw new IllegalArgumentException("삭제할 메인 이미지가 없습니다.");
        }

        deletePhysicalFile(oldImage.getImageUrl());
        adminProductImageMapper.deleteSingleImageByType(productNo, "MAIN");
    }

    @Override
    @Transactional
    public void addGalleryImage(Long productNo, MultipartFile file) {
        validateProduct(productNo);
        validateFile(file);

        String savedFileName = customFileUtil.saveFile(file);
        if (savedFileName == null) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        Integer nextSortOrder = adminProductImageMapper.getNextGallerySortOrder(productNo);

        AdminProductImageDTO dto = new AdminProductImageDTO();
        dto.setProductNo(productNo);
        dto.setImageUrl(savedFileName);
        dto.setImageType("GALLERY");
        dto.setSortOrder(nextSortOrder);

        adminProductImageMapper.insertProductImage(dto);
    }

    @Override
    @Transactional
    public void updateGalleryImage(Long productNo, Long productImgNo, MultipartFile file) {
        validateProduct(productNo);
        validateFile(file);

        if (productImgNo == null) {
            throw new IllegalArgumentException("상품 이미지 번호가 없습니다.");
        }

        AdminProductImageDTO oldImage = adminProductImageMapper.getGalleryImage(productNo, productImgNo);
        if (oldImage == null) {
            throw new IllegalArgumentException("존재하지 않는 갤러리 이미지입니다.");
        }

        String savedFileName = customFileUtil.saveFile(file);
        if (savedFileName == null) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        deletePhysicalFile(oldImage.getImageUrl());

        oldImage.setImageUrl(savedFileName);
        adminProductImageMapper.updateImageUrl(oldImage);
    }

    @Override
    @Transactional
    public void deleteGalleryImage(Long productNo, Long productImgNo) {
        validateProduct(productNo);

        if (productImgNo == null) {
            throw new IllegalArgumentException("상품 이미지 번호가 없습니다.");
        }

        AdminProductImageDTO oldImage = adminProductImageMapper.getGalleryImage(productNo, productImgNo);
        if (oldImage == null) {
            throw new IllegalArgumentException("존재하지 않는 갤러리 이미지입니다.");
        }

        deletePhysicalFile(oldImage.getImageUrl());
        adminProductImageMapper.deleteGalleryImage(productNo, productImgNo);
    }

    @Override
    @Transactional
    public void updateSizeImage(Long productNo, MultipartFile file) {
        validateProduct(productNo);
        validateFile(file);

        AdminProductImageDTO oldImage = adminProductImageMapper.getSingleImageByType(productNo, "SIZE");
        String savedFileName = customFileUtil.saveFile(file);

        if (savedFileName == null) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        if (oldImage == null) {
            AdminProductImageDTO dto = new AdminProductImageDTO();
            dto.setProductNo(productNo);
            dto.setImageUrl(savedFileName);
            dto.setImageType("SIZE");
            dto.setSortOrder(1);

            adminProductImageMapper.insertProductImage(dto);
        } else {
            deletePhysicalFile(oldImage.getImageUrl());

            oldImage.setImageUrl(savedFileName);
            adminProductImageMapper.updateImageUrl(oldImage);
        }
    }

    @Override
    @Transactional
    public void deleteSizeImage(Long productNo) {
        validateProduct(productNo);

        AdminProductImageDTO oldImage = adminProductImageMapper.getSingleImageByType(productNo, "SIZE");
        if (oldImage == null) {
            throw new IllegalArgumentException("삭제할 사이즈표 이미지가 없습니다.");
        }

        deletePhysicalFile(oldImage.getImageUrl());
        adminProductImageMapper.deleteSingleImageByType(productNo, "SIZE");
    }

    private void validateProduct(Long productNo) {
        if (productNo == null) {
            throw new IllegalArgumentException("상품번호가 없습니다.");
        }

        AdminProductDetailDTO product = adminProductMapper.getProduct(productNo);
        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private void deletePhysicalFile(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        customFileUtil.deleteFiles(List.of(imageUrl));
    }
}