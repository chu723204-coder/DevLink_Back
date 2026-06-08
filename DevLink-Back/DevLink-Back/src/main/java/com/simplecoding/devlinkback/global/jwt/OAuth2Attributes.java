package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.michelin_back.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes {

    private String email;
    private String name;
    private String provider;
    private String providerId;

    public static OAuth2Attributes of(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        } else if ("naver".equals(registrationId)) {
            return ofNaver(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile     = (Map<String, Object>) kakaoAccount.get("profile");

        String providerId = String.valueOf(attributes.get("id"));

        // 이메일 동의 여부 체크 — 미동의 시 kakao_account.email 이 null 로 내려올 수 있음
        Boolean emailNeedsAgreement = (Boolean) kakaoAccount.get("email_needs_agreement");
        String email = (emailNeedsAgreement == null || !emailNeedsAgreement)
                ? (String) kakaoAccount.get("email")
                : null;
        // 이메일이 없는 경우 DB nullable=false 제약 위반 방지용 fallback
        if (email == null || email.isBlank()) {
            email = "kakao_" + providerId + "@oauth.local";
        }

        return OAuth2Attributes.builder()
                .email(email)
                .name((String) profile.get("nickname"))
                .provider("kakao")
                .providerId(providerId)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .provider("naver")
                .providerId((String) response.get("id"))
                .build();
    }

    public Member toMember() {
        return Member.builder()
                .loginId(provider + "_" + providerId)
                .loginPw("OAUTH2_USER")
                .email(email)
                .name(name != null ? name : "소셜유저")
                .phone("000-0000-0000")
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
