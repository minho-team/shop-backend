package com.shop.service.user.roulette;

import java.time.LocalDateTime;
import java.util.Map;

public interface RouletteService {
    Map<String, Object> spin(Long memberNo) throws Exception;
    void issueSignupCoupon(Long memberNo) throws Exception;
}