package com.shop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Product;
import com.shop.domain.ProductImage;
import com.shop.domain.ProductOption;
import com.shop.mapper.ProductImageMapper;
import com.shop.mapper.ProductMapper;
import com.shop.mapper.ProductOptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSeedService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductOptionMapper productOptionMapper;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Transactional
    public void seedOneProduct() throws Exception {

        // 1. 상품 객체 생성
        Product product = new Product();
        product.setName("토트 백");
        product.setPrice(19900);
        product.setSalePrice(15900);
        product.setCategoryId(244L); // 네 category 테이블에 실제 존재하는 값으로 바꿔야 함
        product.setDescription("초기 데이터 테스트용 토트 백 상품");
        product.setUseYn("Y");
        product.setViewCount(0);
        product.setSameDayDeliveryYn("N");

        // 2. 상품 INSERT
        productMapper.insertSeedProduct(product);

        // selectKey로 채워진 PK
        Long productNo = product.getProductNo();

        // 3. 이미지 저장 폴더
        Path uploadDir = Path.of(uploadPath,"product");
        
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 4. 원본 이미지들
        Path mainImage = Path.of("C:/upload/seed/Tote_bag1.jpg");
        Path galleryImage1 = Path.of("C:/upload/seed/Tote_bag2.jpg");
        Path galleryImage2 = Path.of("C:/upload/seed/Tote_bag3.jpg");

        // 5. 이미지 저장 + DB insert
        saveImage(mainImage, uploadDir, productNo, "MAIN", 1);
        saveImage(galleryImage1, uploadDir, productNo, "GALLERY", 2);
        saveImage(galleryImage2, uploadDir, productNo, "GALLERY", 3);

        // 6. 옵션도 같이 넣고 싶으면
        insertOption(productNo, "S", "BEIGE", 100);
        insertOption(productNo, "M", "BEIGE", 100);
    }

    private void saveImage(Path sourceFile,
                           Path uploadDir,
                           Long productNo,
                           String imageType,
                           int sortOrder) throws IOException {

        if (!Files.exists(sourceFile)) {
            throw new IllegalArgumentException("원본 이미지 파일이 없습니다: " + sourceFile);
        }

        String originalName = sourceFile.getFileName().toString();
        String storedName = UUID.randomUUID() + "_" + originalName;

        Path targetFile = uploadDir.resolve(storedName);

        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

        ProductImage productImg = new ProductImage();
        productImg.setProductNo(productNo);

        // DB에는 절대경로 말고 상대경로 비슷하게 저장
        productImg.setImageUrl("product/" + storedName);

        productImg.setImageType(imageType);
        productImg.setSortOrder(sortOrder);

        productImageMapper.insertProductImg(productImg);
    }

    private void insertOption(Long productNo, String size, String color, int stock) {
        ProductOption option = new ProductOption();
        option.setProductNo(productNo);
        option.setOptionSize(size);
        option.setColor(color);
        option.setStock(stock);
        option.setUseYn("Y");

        productOptionMapper.insertProductOption(option);
    }
}