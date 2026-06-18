package com.simplecoding.devlinkback.domain.notification.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column
    private String targetUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    public void read() {
        this.isRead = true;
    }

    public enum NotificationType {
        COMMENT,        // 댓글 알림
        LIKE,           // 좋아요 알림
        STUDY_APPLY,    // 스터디 지원 알림
        STUDY_ACCEPT,   // 스터디 수락 알림
        STUDY_REJECT    // 스터디 거절 알림
    }
}