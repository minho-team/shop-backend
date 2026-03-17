package com.shop.service.user.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Review;
import com.shop.dto.user.review.ReviewSaveRequestDTO;
import com.shop.mapper.ReviewMapper;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewMapper reviewMapper;
    private final CustomFileUtil fileUtil; // 주입받기

    @Override
    @Transactional
    public void registerReview(ReviewSaveRequestDTO dto) {
        
        // 1. CustomFileUtil을 사용하여 파일 저장 (C:/upload 폴더에 저장됨)
        // 저장된 파일명(UUID_파일명.jpg)이 리턴됩니다.
        String savedName = fileUtil.saveFile(dto.getUploadFile());

        // 2. VO 객체 생성 및 매핑
        Review review = new Review();
        review.setMemberNo(dto.getMemberNo());
        review.setProductNo(dto.getProductNo());
        review.setOrderItemNo(dto.getOrderItemNo());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        
        // 3. DB에는 파일명만 저장
        review.setImageUrl(savedName);

        reviewMapper.insertReview(review);
    }
}