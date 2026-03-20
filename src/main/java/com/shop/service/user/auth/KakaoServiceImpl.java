package com.shop.service.user.auth;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KakaoServiceImpl implements KakaoService {

    @Value("${kakao.client-id}")
    private String clientId;
    
    @Value("${kakao.client-secret}")
    private String clientSecret;
    
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        Map<String, Object> response = restTemplate.postForObject(tokenUri, request, Map.class);

        log.info("카카오 토큰 응답: {}", response);

        return (String) response.get("access_token");
    }

    @Override
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        org.springframework.http.ResponseEntity<Map> response =
                restTemplate.exchange(userInfoUri,
                        org.springframework.http.HttpMethod.GET,
                        request,
                        Map.class);

        Map<String, Object> body = response.getBody();
        log.info("카카오 사용자 정보: {}", body);

        return body;
    }
}
