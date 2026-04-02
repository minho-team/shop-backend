package com.shop.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CustomFileUtil {

	@Value("${upload.path}")
	private String uploadPath;

	@PostConstruct // 스프링이 이 객체를 만들고 값 주입까지 끝낸 뒤 자동 실행, 서버 시작할때 딱 한번만 실행
	public void init() {
		File tempFolder = new File(uploadPath); // uploadPath 경로를 기준으로 폴더 객체를 생성
		// 폴더가 없으면 새로 생성
		if (tempFolder.exists() == false) {
			tempFolder.mkdirs();
		}
		// 절대 경로로 다시 저장 및 로그 출력
		uploadPath = tempFolder.getAbsolutePath();
		log.info("tempFolder.getAbsolutePath()" + uploadPath);
	}

	// 단일 파일 저장
	public String saveFile(MultipartFile file) {

		// 파일 객체 자체가 없으면 저장 불가
		if (file == null) {
			return null;
		}

		// 파일 내용이 비어 있으면 저장하지 않음
		if (file.isEmpty()) {
			return null;
		}
		
		// 원본 파일명 가져오기
		String originalFilename = file.getOriginalFilename();

		// 원본 파일명이 없거나 공백이면 비정상 파일로 판단
		if (originalFilename == null || originalFilename.trim().isEmpty()) {
			return null;
		}

		// 중복 방지를 위해 UUID를 붙여 저장 파일명 생성
		String savedName = UUID.randomUUID().toString() + "_" + originalFilename;
		// 저장 경로 생성
		Path savePath = Paths.get(uploadPath, savedName);

		try {
			// 실제 파일 저장
			Files.copy(file.getInputStream(), savePath);
			return savedName; // 저장 성공 시 파일명 반환
		} catch (IOException e) {
			// 저장 실패를 상위 계층에 전달
			throw new RuntimeException("File save error: " + e.getMessage(), e);
		}
	}

	// 복수 파일 저장
	public List<String> saveFiles(List<MultipartFile> files) {

		List<String> uploadNames = new ArrayList<>();

		// 파일 리스트가 없거나 비어 있으면 빈 리스트 반환
		if (files == null || files.isEmpty()) {
			return uploadNames;
		}

		for (MultipartFile multipartFile : files) {

			// null 파일 또는 빈 파일은 건너뜀
			if (multipartFile == null || multipartFile.isEmpty()) {
				continue;
			}

			String originalFilename = multipartFile.getOriginalFilename();

			// 파일명이 없거나 공백이면 건너뜀
			if (originalFilename == null || originalFilename.trim().isEmpty()) {
				continue;
			}

			// 중복 방지용 UUID 파일명 생성
			String savedName = UUID.randomUUID().toString() + "_" + originalFilename;
			Path savePath = Paths.get(uploadPath, savedName);

			try {
				Files.copy(multipartFile.getInputStream(), savePath);
				uploadNames.add(savedName);
			} catch (IOException e) {
				throw new RuntimeException("File save error: " + e.getMessage(), e);
			}
		}

		return uploadNames;
	}

	// 브라우저에 이미지를 보여주는 기능
	public ResponseEntity<Resource> getFile(String fileName) {
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

		// 웹브라우저에 보낼 header
		HttpHeaders headers = new HttpHeaders();
		try {
			// Files.probeContentType()은 파일 경로를 분석하여 MIME 타입을 자동 감지 jpg → image/jpeg, png →
			// image/png pdf → application/pdf 이 정보를 HTTP 응답 헤더에 Content-Type으로 추가한다
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	public void deleteFiles(List<String> fileNames) {
		if (fileNames == null || fileNames.size() == 0) {
			return;
		}
		fileNames.forEach(fileName -> {

			// 원본 이미지 경로
			Path filePath = Paths.get(uploadPath, fileName);
			try {
				Files.deleteIfExists(filePath);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

}
