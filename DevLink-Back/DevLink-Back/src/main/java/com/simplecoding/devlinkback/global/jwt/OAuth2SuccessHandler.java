package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Value("${spring.react.ip}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // email 대신 provider+providerId 기반 loginId 로 회원 조회
        // — 카카오 이메일 미동의 등 email 이 null 인 경우에도 안전하게 동작
        String loginId = extractLoginId(oAuth2User);
        log.info("[OAuth2 로그인 성공] loginId={}", loginId);

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 사용자입니다. loginId=" + loginId));

        String accessToken  = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member.getLoginId());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * OAuth2User attributes 에서 DB loginId (= "provider_providerId") 를 추출합니다.
     * CustomOAuth2UserService.saveOrUpdate() 의 toMember() 와 동일한 규칙을 사용합니다.
     */
    @SuppressWarnings("unchecked")
    private String extractLoginId(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오: attributes 최상위에 id(Long) 존재
        if (attributes.containsKey("kakao_account")) {
            return "kakao_" + attributes.get("id");
        }
        // 네이버: attributes.response.id(String)
        if (attributes.containsKey("response")) {
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            return "naver_" + naverResponse.get("id");
        }
        throw new RuntimeException("지원하지 않는 OAuth2 Provider 입니다.");
    }
}
