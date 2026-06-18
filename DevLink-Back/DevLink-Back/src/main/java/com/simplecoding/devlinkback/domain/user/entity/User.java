package com.simplecoding.devlinkback.domain.user.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String provider;

    @Column
    private String providerId;

    @Column(nullable = false)
    @Builder.Default
    private String deleteYn = "N";

    @Column(nullable = false)
    @Builder.Default
    private Boolean banned = false;  // ✅ 계정 정지 필드 추가

    // 닉네임 수정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 비밀번호 수정
    public void updatePassword(String password) {
        this.password = password;
    }

    // 회원 탈퇴
    public void delete() {
        this.deleteYn = "Y";
    }

    // 탈퇴 시 이메일/닉네임 익명화
    public void anonymize(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    // ✅ 계정 정지/해제 토글
    public void toggleBan() {
        this.banned = !this.banned;
    }

    @Getter
    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }
}