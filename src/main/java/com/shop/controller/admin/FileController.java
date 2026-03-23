package com.shop.controller.admin;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final CustomFileUtil customFileUtil;

    @GetMapping("/view")
    public ResponseEntity<Resource> viewFile(@RequestParam("fileName") String fileName) {
        return customFileUtil.getFile(fileName);
    }
}
