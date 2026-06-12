package com.simplecoding.devlinkback.domain.chat.service;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.Message;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomRepository;
import com.simplecoding.devlinkback.domain.chat.repository.MessageRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    // ── 채팅방 생성 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<ChatRoom> createChatRoom(Long studyId, String roomName) {
        // 이미 존재하는 채팅방이면 반환
        return chatRoomRepository.findByStudyId(studyId)
                .map(room -> ApiResponse.success(room, "기존 채팅방 반환"))
                .orElseGet(() -> {
                    ChatRoom chatRoom = ChatRoom.builder()
                            .studyId(studyId)
                            .roomName(roomName)
                            .build();
                    return ApiResponse.success(
                            chatRoomRepository.save(chatRoom), "채팅방 생성 성공");
                });
    }

    // ── 채팅방 목록 조회 ──────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoom>> getChatRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findByDeleteYn("N");
        return ApiResponse.success(rooms, "채팅방 목록 조회 성공");
    }

    // ── 메시지 저장 ──────────────────────────────────────────
    @Transactional
    public Message saveMessage(Long chatRoomId, Long senderId, String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .build();
        return messageRepository.save(message);
    }

    // ── 메시지 내역 조회 ──────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<Message>> getMessages(Long chatRoomId) {
        List<Message> messages =
                messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return ApiResponse.success(messages, "메시지 내역 조회 성공");
    }

    // ── 읽지 않은 메시지 수 ───────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<Long> getUnreadCount(Long chatRoomId, Long userId) {
        Long count = messageRepository
                .countByChatRoomIdAndSenderIdNotAndIsRead(chatRoomId, userId, false);
        return ApiResponse.success(count, "읽지 않은 메시지 수 조회 성공");
    }
}