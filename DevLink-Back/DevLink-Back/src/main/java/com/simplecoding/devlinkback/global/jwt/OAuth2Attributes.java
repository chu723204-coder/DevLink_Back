package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.devlinkback.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes {

    private String provider;
    private String providerId;
    private String email;
    private String nickname;

    public static OAuth2Attributes of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + provider);
        };
    }

    // 카카오
    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.getOrDefault("email",
                attributes.get("id") + "@kakao.local");
        String nickname = (String) profile.getOrDefault("nickname", "kakao_" + attributes.get("id"));

        return OAuth2Attributes.builder()
                .provider("kakao")
                .providerId(String.valueOf(attributes.get("id")))
                .email(email)
                .nickname(nickname)
                .build();
    }

    // 네이버
    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String email = (String) response.getOrDefault("email",
                response.get("id") + "@naver.local");
        String nickname = (String) response.getOrDefault("nickname", "naver_" + response.get("id"));

        return OAuth2Attributes.builder()
                .provider("naver")
                .providerId((String) response.get("id"))
                .email(email)
                .nickname(nickname)
                .build();
    }

    // OAuth2 사용자 → User Entity 변환
    public User toUser() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .role(User.Role.ROLE_USER)
                .build();
    }
}