package com.simplecoding.devlinkback.domain.chat.service;

import com.simplecoding.devlinkback.domain.chat.dto.ChatRoomDto;
import com.simplecoding.devlinkback.domain.chat.dto.MessageDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse<ChatRoom> createChatRoom(Long studyId, String roomName) {
        return chatRoomRepository.findByStudyId(studyId)
                .map(room -> ApiResponse.success(room, "기존 채팅방 반환"))
                .orElseGet(() -> {
                    ChatRoom chatRoom = ChatRoom.builder()
                            .studyId(studyId)
                            .roomName(roomName)
                            .build();
                    return ApiResponse.success(chatRoomRepository.save(chatRoom), "채팅방 생성 성공");
                });
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoom>> getChatRooms() {
        return ApiResponse.success(chatRoomRepository.findByDeleteYn("N"), "채팅방 목록 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoomDto>> getMyChatRooms(Long userId) {
        List<ChatRoomDto> rooms = chatRoomMemberRepository.findByUser_UserId(userId)
                .stream()
                .map(member -> ChatRoomDto.builder()
                        .roomId(member.getChatRoom().getChatRoomId())
                        .roomName(member.getChatRoom().getRoomName())
                        .studyId(member.getChatRoom().getStudyId())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.success(rooms, "내 채팅방 목록 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<ChatRoomMember>> getChatRoomMembers(Long chatRoomId) {
        return ApiResponse.success(
                chatRoomMemberRepository.findByChatRoom_ChatRoomId(chatRoomId), "채팅방 참여자 조회 성공");
    }

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

    @Transactional
    public ApiResponse<Void> leaveChatRoom(Long chatRoomId, Long userId) {
        ChatRoomMember member = chatRoomMemberRepository
                .findByChatRoom_ChatRoomIdAndUser_UserId(chatRoomId, userId)
                .orElseThrow(() -> CommonException.notFound("채팅방 참여 정보를 찾을 수 없습니다."));
        chatRoomMemberRepository.delete(member);
        return ApiResponse.success(null, "채팅방 퇴장 성공");
    }

    @Transactional
    public MessageDto saveMessage(Long chatRoomId, Long senderId, String content) {
        if (!chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(chatRoomId, senderId)) {
            throw CommonException.forbidden("채팅방 참여자만 메시지를 보낼 수 있습니다.");
        }
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .build();
        Message saved = messageRepository.save(message);

        String nickname = userRepository.findById(senderId)
                .map(User::getNickname)
                .orElse("알 수 없음");

        return MessageDto.builder()
                .messageId(saved.getMessageId())
                .chatRoomId(saved.getChatRoomId())
                .senderId(saved.getSenderId())
                .nickname(nickname)
                .content(saved.getContent())
                .isRead(saved.getIsRead())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MessageDto>> getMessages(Long chatRoomId) {
        List<MessageDto> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId)
                .stream()
                .map(m -> {
                    String nickname = userRepository.findById(m.getSenderId())
                            .map(User::getNickname)
                            .orElse("알 수 없음");
                    return MessageDto.builder()
                            .messageId(m.getMessageId())
                            .chatRoomId(m.getChatRoomId())
                            .senderId(m.getSenderId())
                            .nickname(nickname)
                            .content(m.getContent())
                            .isRead(m.getIsRead())
                            .createdAt(m.getCreatedAt())
                            .updatedAt(m.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
        return ApiResponse.success(messages, "메시지 내역 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> getUnreadCount(Long chatRoomId, Long userId) {
        return ApiResponse.success(
                messageRepository.countByChatRoomIdAndSenderIdNotAndIsRead(chatRoomId, userId, false),
                "읽지 않은 메시지 수 조회 성공");
    }
}