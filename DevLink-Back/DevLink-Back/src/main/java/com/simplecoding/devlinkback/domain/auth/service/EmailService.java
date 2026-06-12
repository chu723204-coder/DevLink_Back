package com.simplecoding.devlinkback.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증 코드 임시 저장 (이메일 → 코드)
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();

    // 인증 코드 발송
    public void sendVerificationCode(String email) {
        String code = generateCode();
        codeStore.put(email, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[DevLink] 이메일 인증 코드");
        message.setText("DevLink 이메일 인증 코드입니다.\n\n인증 코드: " + code + "\n\n5분 내에 입력해주세요.");

        try {
            mailSender.send(message);
            log.info("인증 코드 발송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = codeStore.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            codeStore.remove(email);
            return true;
        }
        return false;
    }

    // 6자리 랜덤 코드 생성
    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}