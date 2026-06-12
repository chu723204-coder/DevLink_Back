package com.simplecoding.devlinkback.domain.chat.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    @Builder.Default
    private String deleteYn = "N";

    // 채팅방 삭제
    public void delete() {
        this.deleteYn = "Y";
    }
}