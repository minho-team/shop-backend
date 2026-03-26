package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.Review;
import com.shop.dto.user.product.HomeReviewDto;
import com.shop.dto.user.review.MyReviewResponseDTO;
import com.shop.dto.user.review.ReviewDTO;

@Mapper
public interface ReviewMapper {
	void insertReview(Review review);
	List<Review> getReviewListByProduct(Long productNo);
	Review getOneReviewByOrderItem(Long orderItemNo);
	List<MyReviewResponseDTO> selectReviewsByMemberNo(Long memberNo);
	List<HomeReviewDto> selectHomeRecentReviews();
	void deleteReview(@Param("reviewNo") Long reviewNo, @Param("memberNo") Long memberNo);
	int updateReview(ReviewDTO dto);
}