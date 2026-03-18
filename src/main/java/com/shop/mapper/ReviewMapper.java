package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.shop.domain.Review;

@Mapper
public interface ReviewMapper {
    void insertReview(Review review);
    
    List<Review> getReviewListByProduct(int productNo);
    
    Review getOneReviewByOrderItem(int orderItemNo);
}