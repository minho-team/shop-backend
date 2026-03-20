package com.shop.service.user.auth;

import java.util.Map;

public interface KakaoService {
    String getAccessToken(String code);
    Map<String, Object> getKakaoUserInfo(String accessToken);
}