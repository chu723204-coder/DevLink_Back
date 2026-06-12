package com.simplecoding.devlinkback.domain.notification.repository;

import com.simplecoding.devlinkback.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 내 알림 목록 조회 (최신순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 읽지 않은 알림 수 조회
    Long countByUserIdAndIsRead(Long userId, Boolean isRead);

    // 전체 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(Long userId);
}