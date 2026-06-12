package com.simplecoding.devlinkback.domain.chat.controller;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.ChatRoomMember;
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
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    // ── 채팅방 ───────────────────────────────────────────────

    // 채팅방 전체 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getChatRooms() {
        return ResponseEntity.ok(chatService.getChatRooms());
    }

    // 내가 참여 중인 채팅방 목록 조회
    @GetMapping("/rooms/my")
    public ResponseEntity<ApiResponse<List<ChatRoomMember>>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getMyChatRooms(userDetails.getUserId()));
    }

    // 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(
            @RequestParam Long studyId,
            @RequestParam String roomName) {
        return ResponseEntity.ok(chatService.createChatRoom(studyId, roomName));
    }

    // ── 채팅방 참여자 ─────────────────────────────────────────

    // 채팅방 참여자 목록 조회
    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<List<ChatRoomMember>>> getChatRoomMembers(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getChatRoomMembers(roomId));
    }

    // 채팅방 참여
    @PostMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<ChatRoomMember>> joinChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.joinChatRoom(roomId, userDetails.getUserId()));
    }

    // 채팅방 퇴장
    @DeleteMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.leaveChatRoom(roomId, userDetails.getUserId()));
    }

    // ── 메시지 ────────────────────────────────────────────────

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
}