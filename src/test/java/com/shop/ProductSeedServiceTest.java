package com.shop;

import com.shop.service.ProductSeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.shop.service.ProductSeedService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class ProductSeedServiceTest {

	@Autowired
    private ProductSeedService productSeedService;

    @Test
    void seedProducts() throws Exception {
        productSeedService.seedFromCsv();
    }
}