package com.simplecoding.devlinkback.domain.auth.service;

import com.simplecoding.devlinkback.domain.auth.dto.LoginRequest;
import com.simplecoding.devlinkback.domain.auth.dto.LoginResponse;
import com.simplecoding.devlinkback.domain.auth.dto.SignupRequest;
import com.simplecoding.devlinkback.domain.auth.entity.RefreshToken;
import com.simplecoding.devlinkback.domain.auth.repository.RefreshTokenRepository;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import com.simplecoding.devlinkback.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 회원가입
    @Transactional
    public ApiResponse<Void> signup(SignupRequest request) {
        if (userRepository.existsByEmailAndDeleteYn(request.getEmail(), "N")) {
            return ApiResponse.fail("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNicknameAndDeleteYn(request.getNickname(), "N")) {
            return ApiResponse.fail("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user);
        return ApiResponse.success(null, "회원가입이 완료되었습니다.");
    }

    // 로그인
    @Transactional
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ApiResponse.fail("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        if (user.getDeleteYn().equals("Y")) {
            return ApiResponse.fail("탈퇴한 회원입니다.");
        }

        // ✅ 정지 체크를 비밀번호 체크보다 먼저
        if (user.getBanned()) {
            return ApiResponse.fail("정지된 계정입니다. 관리자에게 문의하세요.");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.fail("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

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

        LoginResponse response = LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .build();

        return ApiResponse.success(response, "로그인 성공");
    }

    // 로그아웃
    @Transactional
    public ApiResponse<Void> logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        return ApiResponse.success(null, "로그아웃 되었습니다.");
    }

    // 토큰 재발급
    @Transactional
    public ApiResponse<String> reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ApiResponse.fail("유효하지 않은 Refresh Token입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElse(null);

        if (savedToken == null || !savedToken.getToken().equals(refreshToken)) {
            return ApiResponse.fail("Refresh Token이 일치하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ApiResponse.fail("사용자를 찾을 수 없습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole().name());
        return ApiResponse.success(newAccessToken, "토큰 재발급 성공");
    }

    // 이메일 인증 코드 발송
    public ApiResponse<Void> sendEmailCode(String email) {
        if (userRepository.existsByEmailAndDeleteYn(email, "N")) {
            return ApiResponse.fail("이미 사용 중인 이메일입니다.");
        }
        emailService.sendVerificationCode(email);
        return ApiResponse.success(null, "인증 코드가 발송되었습니다.");
    }

    // 이메일 인증 코드 검증
    public ApiResponse<Void> verifyEmailCode(String email, String code) {
        if (!emailService.verifyCode(email, code)) {
            return ApiResponse.fail("인증 코드가 올바르지 않습니다.");
        }
        return ApiResponse.success(null, "이메일 인증이 완료되었습니다.");
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public ApiResponse<LoginResponse> getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        LoginResponse response = LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();

        return ApiResponse.success(response, "내 정보 조회 성공");
    }

    // refreshToken 조회 (컨트롤러에서 쿠키 설정용)
    @Transactional(readOnly = true)
    public String getRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .map(RefreshToken::getToken)
                .orElse(null);
    }
}