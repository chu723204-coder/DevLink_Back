package com.simplecoding.devlinkback.domain.chat.repository;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByChatRoom_ChatRoomId(Long chatRoomId);

    List<ChatRoomMember> findByUser_UserId(Long userId);

    boolean existsByChatRoom_ChatRoomIdAndUser_UserId(Long chatRoomId, Long userId);

    Optional<ChatRoomMember> findByChatRoom_ChatRoomIdAndUser_UserId(Long chatRoomId, Long userId);

    long countByChatRoom_ChatRoomId(Long chatRoomId);

    void deleteByChatRoom_ChatRoomId(Long chatRoomId);
}