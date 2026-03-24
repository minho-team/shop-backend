package com.shop.service.user.product;

import com.shop.domain.Product;
import com.shop.domain.ProductImage;
import com.shop.domain.ProductOption;
import com.shop.dto.seed.ProductImageSeedRow;
import com.shop.dto.seed.ProductOptionSeedRow;
import com.shop.dto.seed.ProductSeedRow;
import com.shop.mapper.user.ProductImageMapper;
import com.shop.mapper.user.ProductMapper;
import com.shop.mapper.user.ProductOptionMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        System.out.println("===== seedFromCsv 시작 =====");
        System.out.println("seedRoot = " + seedRoot);
        System.out.println("productCsv = " + productCsv);
        System.out.println("optionCsv = " + optionCsv);
        System.out.println("imageCsv = " + imageCsv);
        System.out.println("seedImageDir = " + seedImageDir);

        validateFileExists(productCsv, "상품 CSV");
        validateFileExists(optionCsv, "옵션 CSV");
        validateFileExists(imageCsv, "이미지 CSV");
        validateDirectoryExists(seedImageDir, "시드 이미지 폴더");

        List<ProductSeedRow> productRows = readProducts(productCsv);
        List<ProductOptionSeedRow> optionRows = readOptions(optionCsv);
        List<ProductImageSeedRow> imageRows = readImages(imageCsv);

        System.out.println("productRows.size = " + productRows.size());
        System.out.println("optionRows.size = " + optionRows.size());
        System.out.println("imageRows.size = " + imageRows.size());

        Map<String, Long> productKeyToProductNo = new HashMap<>();

        // 1. 상품 insert
        for (ProductSeedRow row : productRows) {
            Product product = new Product();
            product.setName(row.getName());
            product.setPrice(row.getPrice());
            product.setDiscountRate(row.getDiscountRate() == null ? 0 : row.getDiscountRate());
            product.setCategoryId(row.getCategoryId());
            product.setDescription(row.getDescription());
            product.setUseYn(defaultYn(row.getUseYn()));
            product.setSameDayDeliveryYn(defaultYn(row.getSameDayDeliveryYn()));

            productMapper.insertSeedProduct(product);

            if (product.getProductNo() == null) {
                throw new IllegalStateException("상품 insert 후 productNo가 채워지지 않았습니다. 상품명: " + row.getName());
            }

            productKeyToProductNo.put(row.getProductKey(), product.getProductNo());
            System.out.println("[상품 insert 완료] productKey=" + row.getProductKey() + ", productNo=" + product.getProductNo());
        }

        // 2. 옵션 insert
        for (ProductOptionSeedRow row : optionRows) {
            Long productNo = productKeyToProductNo.get(row.getProductKey());

            if (productNo == null) {
                throw new IllegalArgumentException("옵션 CSV의 product_key가 products.csv에 없음: " + row.getProductKey());
            }

            ProductOption option = new ProductOption();
            option.setProductNo(productNo);
            option.setOptionSize(trimToNull(row.getOptionSize()));
            option.setColor(trimToNull(row.getColor()));
            option.setStock(row.getStock() == null ? 0 : row.getStock());
            option.setUseYn(defaultYn(row.getUseYn()));

            productOptionMapper.insertSeedOption(option);
            System.out.println("[옵션 insert 완료] productKey=" + row.getProductKey()
                    + ", size=" + row.getOptionSize()
                    + ", color=" + row.getColor());
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

            ProductImage productImg = new ProductImage();
            productImg.setProductNo(productNo);
            productImg.setImageUrl(savedFileName);
            productImg.setImageType(trimToNull(row.getImageType()));
            productImg.setSortOrder(row.getSortOrder() == null ? 0 : row.getSortOrder());

            productImgMapper.insertSeedProductImg(productImg);
            System.out.println("[이미지 insert 완료] productKey=" + row.getProductKey()
                    + ", file=" + row.getImageFileName()
                    + ", savedFileName=" + savedFileName);
        }

        System.out.println("===== seedFromCsv 종료 =====");
    }

    private List<ProductSeedRow> readProducts(Path csvPath) throws IOException {
        List<ProductSeedRow> list = new ArrayList<>();

        try (
                Reader reader = newBomSafeReader(csvPath);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(false)
                        .setTrim(true)
                        .build()
                        .parse(reader)
        ) {
            validateHeaders(parser,
                    "product_key",
                    "name",
                    "price",
                    "discount_rate",
                    "category_id",
                    "description",
                    "use_yn",
                    "same_day_delivery_yn"
            );

            for (CSVRecord record : parser) {
                ProductSeedRow row = new ProductSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setName(record.get("name"));
                row.setPrice(parseLong(record.get("price")));
                row.setDiscountRate(parseIntOrDefault(record.get("discount_rate"), 0));
                row.setCategoryId(parseLong(record.get("category_id")));
                row.setDescription(record.get("description"));
                row.setUseYn(defaultYn(record.get("use_yn")));
                row.setSameDayDeliveryYn(defaultYn(record.get("same_day_delivery_yn")));
                list.add(row);
            }
        }

        return list;
    }

    private List<ProductOptionSeedRow> readOptions(Path csvPath) throws IOException {
        List<ProductOptionSeedRow> list = new ArrayList<>();

        try (
                Reader reader = newBomSafeReader(csvPath);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(false)
                        .setTrim(true)
                        .build()
                        .parse(reader)
        ) {
            validateHeaders(parser,
                    "product_key",
                    "option_size",
                    "color",
                    "stock",
                    "use_yn"
            );

            for (CSVRecord record : parser) {
                ProductOptionSeedRow row = new ProductOptionSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setOptionSize(record.get("option_size"));
                row.setColor(record.get("color"));
                row.setStock(parseIntOrDefault(record.get("stock"), 0));
                row.setUseYn(defaultYn(record.get("use_yn")));
                list.add(row);
            }
        }

        return list;
    }

    private List<ProductImageSeedRow> readImages(Path csvPath) throws IOException {
        List<ProductImageSeedRow> list = new ArrayList<>();

        try (
                Reader reader = newBomSafeReader(csvPath);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(false)
                        .setTrim(true)
                        .build()
                        .parse(reader)
        ) {
            validateHeaders(parser,
                    "product_key",
                    "image_file_name",
                    "image_type",
                    "sort_order"
            );

            for (CSVRecord record : parser) {
                ProductImageSeedRow row = new ProductImageSeedRow();
                row.setProductKey(record.get("product_key"));
                row.setImageFileName(record.get("image_file_name"));
                row.setImageType(record.get("image_type"));
                row.setSortOrder(parseIntOrDefault(record.get("sort_order"), 0));
                list.add(row);
            }
        }

        return list;
    }

    /**
     * UTF-8 BOM이 붙은 CSV도 안전하게 읽기 위한 Reader
     */
    private Reader newBomSafeReader(Path csvPath) throws IOException {
        InputStream is = Files.newInputStream(csvPath);
        PushbackInputStream pushbackInputStream = new PushbackInputStream(is, 3);

        byte[] bom = new byte[3];
        int read = pushbackInputStream.read(bom, 0, 3);

        if (read == 3) {
            boolean isUtf8Bom =
                    (bom[0] & 0xFF) == 0xEF &&
                    (bom[1] & 0xFF) == 0xBB &&
                    (bom[2] & 0xFF) == 0xBF;

            if (!isUtf8Bom) {
                pushbackInputStream.unread(bom, 0, 3);
            }
        } else if (read > 0) {
            pushbackInputStream.unread(bom, 0, read);
        }

        return new InputStreamReader(pushbackInputStream, StandardCharsets.UTF_8);
    }

    private void validateHeaders(CSVParser parser, String... requiredHeaders) {
        Map<String, Integer> headerMap = parser.getHeaderMap();

        for (String header : requiredHeaders) {
            if (!headerMap.containsKey(header)) {
                throw new IllegalArgumentException(
                        "CSV 헤더가 올바르지 않습니다. 누락된 헤더: " + header
                                + " / 실제 헤더 목록: " + headerMap.keySet()
                );
            }
        }
    }

    private void validateFileExists(Path path, String label) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException(label + " 파일이 존재하지 않습니다: " + path);
        }
    }

    private void validateDirectoryExists(Path path, String label) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException(label + " 경로가 올바르지 않습니다: " + path);
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.parseLong(value.trim());
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value.trim());
    }

    private Integer parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        return Integer.parseInt(value.trim());
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String defaultYn(String value) {
        if (value == null || value.isBlank()) {
            return "N";
        }
        return value.trim().toUpperCase();
    }
}