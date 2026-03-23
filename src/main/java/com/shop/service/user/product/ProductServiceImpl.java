package com.shop.service.user.product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Product;
import com.shop.domain.ProductOption;
import com.shop.dto.user.product.HomeMainResponse;
import com.shop.dto.user.product.HomeProductCardDto;
import com.shop.dto.user.product.HomeReviewDto;
import com.shop.dto.user.product.PopularKeywordDto;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductDetailResponse;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.dto.user.product.ProductUpdateRequest;
import com.shop.mapper.user.ProductMapper;
import com.shop.mapper.user.ProductOptionMapper;
import com.shop.mapper.user.ReviewMapper;

@Service
public class ProductServiceImpl implements ProductService {

    private static final List<String> SPRING_SALE_KEYWORDS = Arrays.asList(
            "가디건", "니트", "스웨트셔츠", "셔츠", "긴팔", "긴바지"
    );
    private static final int SPRING_SALE_RATE = 10;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductOptionMapper productOptionMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public void insertProduct(ProductCreateRequest dto) throws Exception {
        productMapper.insertProduct(dto);
    }

    @Override
    public void updateProducts(Long productNo, ProductUpdateRequest dto) throws Exception {
        productMapper.updateProducts(productNo, dto);
    }

    @Override
    public void deleteProduct(Long productNo) throws Exception {
        productMapper.deleteProduct(productNo);
    }

    
    //상품 상세페이지에서 상품의 정보(product)와 상품 옵션(product_option)을 가져옴 
    @Override
    public ProductDetailResponse getOneProduct(Long productNo) throws Exception {
        // 조회수 증가
        productMapper.incrementViewCount(productNo);

        Product product = productMapper.getOneProducts(productNo);
        List<ProductOption> options = productOptionMapper.getOptionsByProductNo(productNo);

        ProductDetailResponse response = new ProductDetailResponse();
        response.setProduct(product);
        response.setOptions(options);

        return response;
    }

    @Override
    public List<ProductListResponse> getAllProductToMainPage() throws Exception {
        List<ProductListResponse> list = productMapper.getAllProductToMainPage();

        for (ProductListResponse dto : list) {
            if (dto.getPrice() != null) {
                dto.setSalePrice(calculateSalePrice(dto.getPrice(), dto.getDiscountRate()));
            }

            if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
                dto.setImageUrl("/upload/" + dto.getImageUrl());
            }
        }

        return list;
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        return productMapper.getAllProducts();
    }

    @Override
    public List<ProductListResponseDto> selectSearchProductList(
            Integer categoryId,
            String keyword,
            String sort,
            Boolean discountOnly
    ) throws Exception {
        List<ProductListResponseDto> list =
                productMapper.selectSearchProductList(categoryId, keyword, sort, false);

        for (ProductListResponseDto dto : list) {
            if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
                dto.setImageUrl("/upload/" + dto.getImageUrl());
            }
        }

        if (Boolean.TRUE.equals(discountOnly)) {
            list = list.stream()
                    .filter(dto -> dto.getDiscountRate() != null && dto.getDiscountRate() > 0)
                    .collect(Collectors.toList());
        }

        if ("sale".equals(sort)) {
            list.sort(Comparator.comparing(ProductListResponseDto::getDiscountRate,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return list;
    }

    @Override
    public HomeMainResponse getHomeMainData() throws Exception {
        HomeMainResponse response = new HomeMainResponse();

        List<HomeProductCardDto> newProducts = productMapper.selectHomeNewProducts();
        List<HomeProductCardDto> bestProducts = productMapper.selectHomeBestProducts();
        List<HomeProductCardDto> recommendProducts = productMapper.selectHomeRecommendProducts();
        List<HomeProductCardDto> saleProducts = productMapper.selectHomeSaleProducts();
        // 모든 할인율은 DB의 discount_rate 컬럼 값을 직접 사용

        List<HomeReviewDto> recentReviews = reviewMapper.selectHomeRecentReviews();
        List<PopularKeywordDto> popularKeywords = productMapper.selectPopularKeywords();

        normalizeImagePath(newProducts);
        normalizeImagePath(bestProducts);
        normalizeImagePath(saleProducts);
        normalizeImagePath(recommendProducts);

        response.setNewProducts(newProducts);
        response.setBestProducts(bestProducts);
        response.setSaleProducts(saleProducts);
        response.setRecommendProducts(recommendProducts);
        response.setRecentReviews(recentReviews);
        response.setPopularKeywords(popularKeywords);

        return response;
    }

    private void normalizeImagePath(List<HomeProductCardDto> list) {
        for (HomeProductCardDto dto : list) {
            if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
                dto.setImageUrl("/upload/" + dto.getImageUrl());
            }
        }
    }

    private void applySeasonalDiscount(Product product) {
        if (product == null) {
            return;
        }
        product.setDiscountRate(resolveSeasonalDiscountRate(product.getName()));
    }

    private void applySeasonalDiscount(ProductListResponse product) {
        if (product == null) {
            return;
        }
        int discountRate = resolveSeasonalDiscountRate(product.getName());
        product.setDiscountRate(discountRate);

        if (product.getPrice() != null) {
            product.setSalePrice(calculateSalePrice(product.getPrice(), discountRate));
        }
    }

    private void applySeasonalDiscount(ProductListResponseDto product) {
        if (product == null) {
            return;
        }
        product.setDiscountRate(resolveSeasonalDiscountRate(product.getName()));
    }

    private void applySeasonalDiscount(HomeProductCardDto product) {
        if (product == null) {
            return;
        }
        product.setDiscountRate(resolveSeasonalDiscountRate(product.getName()));
    }

    private HomeProductCardDto toHomeProductCardDto(ProductListResponseDto product) {
        HomeProductCardDto dto = new HomeProductCardDto();
        dto.setProductNo(product.getProductNo());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscountRate(product.getDiscountRate());
        dto.setImageUrl(product.getImageUrl());
        dto.setSameDayDeliveryYn(product.getSameDayDeliveryYn());
        return dto;
    }

    private int resolveSeasonalDiscountRate(String productName) {
        if (productName == null || productName.isBlank()) {
            return 0;
        }

        return SPRING_SALE_KEYWORDS.stream()
                .anyMatch(productName::contains)
                ? SPRING_SALE_RATE
                : 0;
    }

    private Long calculateSalePrice(Long price, Integer discountRate) {
        if (price == null) {
            return null;
        }
        if (discountRate == null || discountRate <= 0) {
            return price;
        }
        return price * (100 - discountRate) / 100;
    }
}