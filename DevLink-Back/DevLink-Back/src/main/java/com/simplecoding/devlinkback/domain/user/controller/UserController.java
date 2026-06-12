package com.simplecoding.devlinkback.domain.user.controller;

import com.simplecoding.devlinkback.domain.post.entity.Post;
import com.simplecoding.devlinkback.domain.study.entity.Study;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.service.UserService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getUserId()));
    }

    // 닉네임 수정
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<User>> updateNickname(
            @RequestParam String nickname,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                userService.updateNickname(userDetails.getUserId(), nickname));
    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                userService.updatePassword(userDetails.getUserId(), currentPassword, newPassword));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.deleteUser(userDetails.getUserId()));
    }

    // 내 게시글 목록
    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<List<Post>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyPosts(userDetails.getUserId()));
    }

    // 내 스터디 목록
    @GetMapping("/me/studies")
    public ResponseEntity<ApiResponse<List<Study>>> getMyStudies(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyStudies(userDetails.getUserId()));
    }
}