package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.devlinkback.domain.auth.entity.RefreshToken;
import com.simplecoding.devlinkback.domain.auth.repository.RefreshTokenRepository;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.cookie-max-age}")
    private int cookieMaxAge;

    private static final String REDIRECT_URI = "http://localhost:5173/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String provider = determineProvider(oAuth2User);
        String providerId = determineProviderId(oAuth2User, provider);

        // findByProviderAndProviderId 로 찾고 없으면 email 로 찾고 그것도 없으면 예외
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // OAuth2Attributes 로 email 추출해서 재시도
                    OAuth2Attributes attrs = OAuth2Attributes.of(provider, oAuth2User.getAttributes());
                    return userRepository.findByEmail(attrs.getEmail())
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                });

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        refreshTokenRepository.findByUserId(user.getUserId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getUserId())
                                        .token(refreshToken)
                                        .build()
                        )
                );

        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);

        String redirectUrl = REDIRECT_URI + "?accessToken=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String determineProvider(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes.containsKey("kakao_account")) {
            return "kakao";
        }
        return "naver";
    }

    @SuppressWarnings("unchecked")
    private String determineProviderId(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }
}