package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UploadSizeTestConfig implements CommandLineRunner {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("max-file-size = " + maxFileSize);
        System.out.println("max-request-size = " + maxRequestSize);
    }
}