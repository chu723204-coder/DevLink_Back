package com.simplecoding.devlinkback.domain.notification.service;

import com.simplecoding.devlinkback.domain.notification.entity.Notification;
import com.simplecoding.devlinkback.domain.notification.repository.NotificationRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // SSE 연결 관리 (userId → SseEmitter)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 타임아웃 30분
    private static final Long SSE_TIMEOUT = 30 * 60 * 1000L;

    // ── SSE 구독 ──────────────────────────────────────────────
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitters.put(userId, emitter);

        // 연결 종료 시 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // 연결 직후 더미 이벤트 전송 (503 방지)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 완료"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // ── 알림 발송 ──────────────────────────────────────────────
    @Transactional
    public void sendNotification(Long userId, Notification.NotificationType type,
                                 String message, String targetUrl) {
        // DB 저장
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .targetUrl(targetUrl)
                .build();
        notificationRepository.save(notification);

        // SSE 전송
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    // ── 알림 목록 조회 ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<Notification>> getNotifications(Long userId) {
        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ApiResponse.success(notifications, "알림 목록 조회 성공");
    }

    // ── 단건 읽음 처리 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElse(null);

        if (notification == null || !notification.getUserId().equals(userId)) {
            return ApiResponse.fail("알림을 찾을 수 없습니다.");
        }

        notification.read();
        return ApiResponse.success(null, "읽음 처리 완료");
    }

    // ── 전체 읽음 처리 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> readAllNotifications(Long userId) {
        notificationRepository.markAllAsRead(userId);
        return ApiResponse.success(null, "전체 읽음 처리 완료");
    }

    // ── 읽지 않은 알림 수 ──────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<Long> getUnreadCount(Long userId) {
        Long count = notificationRepository.countByUserIdAndIsRead(userId, false);
        return ApiResponse.success(count, "읽지 않은 알림 수 조회 성공");
    }
}