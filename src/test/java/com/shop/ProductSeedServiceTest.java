package com.shop;

import com.shop.service.ProductSeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductSeedServiceTest {

    @Autowired
    private ProductSeedService productSeedService;

    @Test
    void 초기상품_넣기() throws Exception {
        productSeedService.seedOneProduct();
    }
}