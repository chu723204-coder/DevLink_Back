package com.simplecoding.devlinkback.domain.auth.controller;

import com.simplecoding.devlinkback.domain.auth.dto.LoginRequest;
import com.simplecoding.devlinkback.domain.auth.dto.LoginResponse;
import com.simplecoding.devlinkback.domain.auth.dto.SignupRequest;
import com.simplecoding.devlinkback.domain.auth.service.AuthService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.cookie-max-age}")
    private int cookieMaxAge;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        ApiResponse<LoginResponse> result = authService.login(request);

        // refreshToken 쿠키 설정
        if (result.getData() != null) {
            String refreshToken = authService.getRefreshToken(result.getData().getUserId());
            Cookie cookie = new Cookie(cookieName, refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // 로컬 개발 환경 - 배포 시 true로 변경
            cookie.setPath("/");
            cookie.setMaxAge(cookieMaxAge);
            response.addCookie(cookie);
        }

        return ResponseEntity.ok(result);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response) {
        authService.logout(userDetails.getUserId());

        // 쿠키 삭제
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 되었습니다."));
    }

    // 토큰 재발급 (쿠키에서 refreshToken 읽기)
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(
            HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.ok(ApiResponse.fail("Refresh Token이 없습니다."));
        }
        return ResponseEntity.ok(authService.reissue(refreshToken));
    }

    // 이메일 인증 코드 발송
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(
            @RequestParam String email) {
        return ResponseEntity.ok(authService.sendEmailCode(email));
    }

    // 이메일 인증 코드 검증
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(
            @RequestParam String email,
            @RequestParam String code) {
        return ResponseEntity.ok(authService.verifyEmailCode(email, code));
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(authService.getMyInfo(userDetails.getUserId()));
    }
}