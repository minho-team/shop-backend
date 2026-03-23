package com.shop.mapper.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.shop.domain.Review;
import com.shop.dto.user.product.HomeReviewDto;
import com.shop.dto.user.review.MyReviewResponseDTO;

@Mapper
public interface ReviewMapper {
	void insertReview(Review review);
	List<Review> getReviewListByProduct(Long productNo);
	Review getOneReviewByOrderItem(Long orderItemNo);
	List<MyReviewResponseDTO> selectReviewsByMemberNo(Long memberNo);
	List<HomeReviewDto> selectHomeRecentReviews();
}