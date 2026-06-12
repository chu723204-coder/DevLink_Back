package com.simplecoding.devlinkback.domain.chat.service;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.ChatRoomMember;
import com.simplecoding.devlinkback.domain.chat.entity.Message;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomMemberRepository;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomRepository;
import com.simplecoding.devlinkback.domain.chat.repository.MessageRepository;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // ── 채팅방 생성 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<ChatRoom> createChatRoom(Long studyId, String roomName) {
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

    // ── 채팅방 전체 목록 조회 ─────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoom>> getChatRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findByDeleteYn("N");
        return ApiResponse.success(rooms, "채팅방 목록 조회 성공");
    }

    // ── 내가 참여 중인 채팅방 목록 조회 ─────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoomMember>> getMyChatRooms(Long userId) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findByUser_UserId(userId);
        return ApiResponse.success(members, "내 채팅방 목록 조회 성공");
    }

    // ── 채팅방 참여자 목록 조회 ──────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoomMember>> getChatRoomMembers(Long chatRoomId) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoom_ChatRoomId(chatRoomId);
        return ApiResponse.success(members, "채팅방 참여자 조회 성공");
    }

    // ── 채팅방 참여 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<ChatRoomMember> joinChatRoom(Long chatRoomId, Long userId) {
        if (chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(chatRoomId, userId)) {
            throw CommonException.badRequest("이미 채팅방에 참여 중입니다.");
        }
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> CommonException.notFound("채팅방을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        return ApiResponse.success(chatRoomMemberRepository.save(member), "채팅방 참여 성공");
    }

    // ── 채팅방 퇴장 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> leaveChatRoom(Long chatRoomId, Long userId) {
        ChatRoomMember member = chatRoomMemberRepository
                .findByChatRoom_ChatRoomIdAndUser_UserId(chatRoomId, userId)
                .orElseThrow(() -> CommonException.notFound("채팅방 참여 정보를 찾을 수 없습니다."));
        chatRoomMemberRepository.delete(member);
        return ApiResponse.success(null, "채팅방 퇴장 성공");
    }

    // ── 메시지 저장 ──────────────────────────────────────────
    @Transactional
    public Message saveMessage(Long chatRoomId, Long senderId, String content) {
        if (!chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(chatRoomId, senderId)) {
            throw CommonException.forbidden("채팅방 참여자만 메시지를 보낼 수 있습니다.");
        }
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