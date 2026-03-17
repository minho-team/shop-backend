package com.shop.mapper;

import com.shop.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReviewMapper {
    
    // 1. 리뷰 등록 (Service에서 넘겨준 Review 객체 저장)
    int insertReview(Review review);

    // 2. 상품 상세페이지용 리뷰 목록 조회 (최신순)
    List<Review> getReviewListByProduct(int productNo);

    // 3. 마이페이지용 내가 쓴 리뷰 목록 조회
    List<Review> getMyReviewList(int memberNo);

    // 4. 리뷰 상세 보기 (마이페이지에서 클릭 시)
    Review getReviewDetail(int reviewNo);
}