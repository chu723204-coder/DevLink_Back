package com.simplecoding.devlinkback.domain.chat.repository;

import com.simplecoding.devlinkback.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 채팅방 메시지 목록 조회 (최신순)
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 읽지 않은 메시지 수 조회
    Long countByChatRoomIdAndSenderIdNotAndIsRead(Long chatRoomId, Long senderId, Boolean isRead);
}