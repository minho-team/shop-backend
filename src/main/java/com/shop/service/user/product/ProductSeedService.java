package com.shop.service.user.product;

import com.shop.dto.seed.ProductImageSeedRow;
import com.shop.dto.seed.ProductOptionSeedRow;
import com.shop.dto.seed.ProductSeedRow;
import com.shop.mapper.ProductImageMapper;
import com.shop.mapper.ProductMapper;
import com.shop.mapper.ProductOptionMapper;
import com.shop.domain.Product;
import com.shop.domain.ProductImage;
import com.shop.domain.ProductOption;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductSeedService {

    @Value("${upload.path}")
    private String uploadPath;

    private final ProductMapper productMapper;
    private final ProductOptionMapper productOptionMapper;
    private final ProductImageMapper productImgMapper;

    @Transactional
    public void seedFromCsv() throws Exception {

        Path seedRoot = Paths.get(uploadPath, "seed");
        Path productCsv = seedRoot.resolve("products.csv");
        Path optionCsv = seedRoot.resolve("product_options.csv");
        Path imageCsv = seedRoot.resolve("product_images.csv");
        Path seedImageDir = seedRoot.resolve("images");

        List<ProductSeedRow> productRows = readProducts(productCsv);
        List<ProductOptionSeedRow> optionRows = readOptions(optionCsv);
        List<ProductImageSeedRow> imageRows = readImages(imageCsv);

        Map<String, Long> productKeyToProductNo = new HashMap<>();

        // 1. 상품 insert
        for (ProductSeedRow row : productRows) {
            Product product = new Product();
            product.setName(row.getName());
            product.setPrice(row.getPrice());
            product.setSalePrice(row.getSalePrice());
            product.setCategoryId(row.getCategoryId());
            product.setDescription(row.getDescription());
            product.setUseYn(row.getUseYn());
            product.setSameDayDeliveryYn(row.getSameDayDeliveryYn());

            productMapper.insertSeedProduct(product); // insert 후 productNo 채워지게
            productKeyToProductNo.put(row.getProductKey(), product.getProductNo());
        }

        // 2. 옵션 insert
        for (ProductOptionSeedRow row : optionRows) {
            Long productNo = productKeyToProductNo.get(row.getProductKey());
            if (productNo == null) {
                throw new IllegalArgumentException("옵션 CSV의 product_key가 products.csv에 없음: " + row.getProductKey());
            }

            ProductOption option = new ProductOption();
            option.setProductNo(productNo);
            option.setOptionSize(row.getOptionSize());
            option.setColor(row.getColor());
            option.setStock(row.getStock());
            option.setUseYn(row.getUseYn());

            productOptionMapper.insertSeedOption(option);
        }

     // 3. 이미지 복사 + DB insert
        for (ProductImageSeedRow row : imageRows) {
            Long productNo = productKeyToProductNo.get(row.getProductKey());
            if (productNo == null) {
                throw new IllegalArgumentException("이미지 CSV의 product_key가 products.csv에 없음: " + row.getProductKey());
            }

            Path source = seedImageDir.resolve(row.getImageFileName());
            if (!Files.exists(source)) {
                throw new IllegalArgumentException("시드 이미지 파일이 없음: " + source);
            }

            Path targetDir = Paths.get(uploadPath);
            Files.createDirectories(targetDir);

            String savedFileName = UUID.randomUUID() + "_" + row.getImageFileName();
            Path target = targetDir.resolve(savedFileName);

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            String dbImageUrl = savedFileName;

            ProductImage productImg = new ProductImage();
            productImg.setProductNo(productNo);
            productImg.setImageUrl(dbImageUrl);
            productImg.setImageType(row.getImageType());
            productImg.setSortOrder(row.getSortOrder());

            productImgMapper.insertSeedProductImg(productImg);
        }
    }

    private List<ProductSeedRow> readProducts(Path csvPath) throws IOException {
        List<ProductSeedRow> list = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                ProductSeedRow row = new ProductSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setName(record.get("name"));
                row.setPrice(parseLong(record.get("price")));
                row.setSalePrice(parseLong(record.get("sale_price")));
                row.setCategoryId(parseLong(record.get("category_id")));
                row.setDescription(record.get("description"));
                row.setUseYn(record.get("use_yn"));
                row.setSameDayDeliveryYn(record.get("same_day_delivery_yn"));
                list.add(row);
            }
        }

        return list;
    }

    private List<ProductOptionSeedRow> readOptions(Path csvPath) throws IOException {
        List<ProductOptionSeedRow> list = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                ProductOptionSeedRow row = new ProductOptionSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setOptionSize(record.get("option_size"));
                row.setColor(record.get("color"));
                row.setStock(parseInt(record.get("stock")));
                row.setUseYn(record.get("use_yn"));
                list.add(row);
            }
        }

        return list;
    }

    private List<ProductImageSeedRow> readImages(Path csvPath) throws IOException {
        List<ProductImageSeedRow> list = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                ProductImageSeedRow row = new ProductImageSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setImageFileName(record.get("image_file_name"));
                row.setImageType(record.get("image_type"));
                row.setSortOrder(parseInt(record.get("sort_order")));
                list.add(row);
            }
        }

        return list;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.parseLong(value.trim());
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value.trim());
    }
}