package com.simplecoding.devlinkback.domain.chat.controller;

import com.simplecoding.devlinkback.domain.chat.dto.ChatRoomDto;
import com.simplecoding.devlinkback.domain.chat.dto.MessageDto;
import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.ChatRoomMember;
import com.simplecoding.devlinkback.domain.chat.service.ChatService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload String content,
            Principal principal) {
        CustomUserDetails userDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        MessageDto saved = chatService.saveMessage(roomId, userDetails.getUserId(), content);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, saved);
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getChatRooms() {
        return ResponseEntity.ok(chatService.getChatRooms());
    }

    @GetMapping("/rooms/my")
    public ResponseEntity<ApiResponse<List<ChatRoomDto>>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getMyChatRooms(userDetails.getUserId()));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(
            @RequestParam Long studyId,
            @RequestParam String roomName) {
        return ResponseEntity.ok(chatService.createChatRoom(studyId, roomName));
    }

    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<List<ChatRoomMember>>> getChatRoomMembers(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getChatRoomMembers(roomId));
    }

    @PostMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<ChatRoomMember>> joinChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.joinChatRoom(roomId, userDetails.getUserId()));
    }

    @DeleteMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.leaveChatRoom(roomId, userDetails.getUserId()));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessages(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId));
    }

    @GetMapping("/rooms/{roomId}/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getUnreadCount(roomId, userDetails.getUserId()));
    }
}