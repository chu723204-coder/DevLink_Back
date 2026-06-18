package com.simplecoding.devlinkback.domain.admin.controller;

import com.simplecoding.devlinkback.domain.admin.dto.AdminStatsDto;
import com.simplecoding.devlinkback.domain.admin.service.AdminService;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // 통계 조회
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    // 강제 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deleteUser(userId));
    }

    // 계정 정지/해제
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleBan(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleBan(userId));
    }

    // 게시글 강제 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok(adminService.deletePost(postId));
    }

    // 스터디 강제 마감
    @PatchMapping("/studies/{studyId}/close")
    public ResponseEntity<ApiResponse<Void>> closeStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(adminService.closeStudy(studyId));
    }
}