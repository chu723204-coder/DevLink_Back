package com.simplecoding.devlinkback.domain.notice.controller;

import com.simplecoding.devlinkback.domain.notice.dto.NoticeResponseDto;
import com.simplecoding.devlinkback.domain.notice.service.NoticeService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회 (누구나)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getNotices() {
        return ResponseEntity.ok(noticeService.getNotices());
    }

    // 공지사항 상세 조회 (누구나)
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getNotice(
            @PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    // 공지사항 작성 (관리자)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> createNotice(
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                noticeService.createNotice(userDetails.getUserId(), title, content));
    }

    // 공지사항 수정 (관리자)
    @PutMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> updateNotice(
            @PathVariable Long noticeId,
            @RequestParam String title,
            @RequestParam String content) {
        return ResponseEntity.ok(noticeService.updateNotice(noticeId, title, content));
    }

    // 공지사항 삭제 (관리자)
    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.deleteNotice(noticeId));
    }
}