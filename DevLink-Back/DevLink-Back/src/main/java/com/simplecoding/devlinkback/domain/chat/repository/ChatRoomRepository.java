package com.simplecoding.devlinkback.domain.chat.repository;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 스터디 ID로 채팅방 조회
    Optional<ChatRoom> findByStudyId(Long studyId);

    // 삭제되지 않은 채팅방 목록
    List<ChatRoom> findByDeleteYn(String deleteYn);
}