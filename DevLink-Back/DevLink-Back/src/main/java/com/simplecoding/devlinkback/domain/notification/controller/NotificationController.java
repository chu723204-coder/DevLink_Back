package com.simplecoding.devlinkback.domain.notification.controller;

import com.simplecoding.devlinkback.domain.notification.entity.Notification;
import com.simplecoding.devlinkback.domain.notification.service.NotificationService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 구독
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.subscribe(userDetails.getUserId());
    }

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getNotifications(userDetails.getUserId()));
    }

    // 단건 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> readNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                notificationService.readNotification(userDetails.getUserId(), notificationId));
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.readAllNotifications(userDetails.getUserId()));
    }

    // 읽지 않은 알림 수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getUnreadCount(userDetails.getUserId()));
    }
}