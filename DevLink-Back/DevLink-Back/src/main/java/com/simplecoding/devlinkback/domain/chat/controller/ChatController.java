package com.simplecoding.devlinkback.domain.chat.controller;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.Message;
import com.simplecoding.devlinkback.domain.chat.service.ChatService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket - 메시지 전송
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Message message = chatService.saveMessage(roomId, userDetails.getUserId(), content);

        // 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getChatRooms() {
        return ResponseEntity.ok(chatService.getChatRooms());
    }

    // 메시지 내역 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> getMessages(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId));
    }

    // 읽지 않은 메시지 수 조회
    @GetMapping("/rooms/{roomId}/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                chatService.getUnreadCount(roomId, userDetails.getUserId()));
    }

    // 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(
            @RequestParam Long studyId,
            @RequestParam String roomName) {
        return ResponseEntity.ok(chatService.createChatRoom(studyId, roomName));
    }
}