package com.simplecoding.devlinkback.domain.study.controller;

import com.simplecoding.devlinkback.domain.study.entity.Study;
import com.simplecoding.devlinkback.domain.study.entity.StudyApply;
import com.simplecoding.devlinkback.domain.study.service.StudyService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    // ── 스터디 ────────────────────────────────────────────────

    // 스터디 전체 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Study>>> getStudies(
            @RequestParam(required = false) Boolean openOnly) {
        if (Boolean.TRUE.equals(openOnly)) {
            return ResponseEntity.ok(studyService.getOpenStudies());
        }
        return ResponseEntity.ok(studyService.getStudies());
    }

    // 스터디 상세 조회
    @GetMapping("/{studyId}")
    public ResponseEntity<ApiResponse<Study>> getStudy(
            @PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getStudy(studyId));
    }

    // 스터디 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Study>> createStudy(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String techStacks,
            @RequestParam Integer maxMembers,
            @RequestParam(required = false) String deadline,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.createStudy(userDetails.getUserId(), title, description,
                        techStacks, maxMembers, deadline));
    }

    // 스터디 수정
    @PutMapping("/{studyId}")
    public ResponseEntity<ApiResponse<Study>> updateStudy(
            @PathVariable Long studyId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String techStacks,
            @RequestParam Integer maxMembers,
            @RequestParam(required = false) String deadline,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.updateStudy(studyId, userDetails.getUserId(), title,
                        description, techStacks, maxMembers, deadline));
    }

    // 스터디 삭제
    @DeleteMapping("/{studyId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.deleteStudy(studyId, userDetails.getUserId()));
    }

    // 모집 마감
    @PatchMapping("/{studyId}/close")
    public ResponseEntity<ApiResponse<Void>> closeStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.closeStudy(studyId, userDetails.getUserId()));
    }

    // ── 지원 ──────────────────────────────────────────────────

    // 지원자 목록 조회
    @GetMapping("/{studyId}/applies")
    public ResponseEntity<ApiResponse<List<StudyApply>>> getApplies(
            @PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getApplies(studyId));
    }

    // 지원하기
    @PostMapping("/{studyId}/apply")
    public ResponseEntity<ApiResponse<StudyApply>> apply(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.apply(studyId, userDetails.getUserId()));
    }

    // 수락
    @PatchMapping("/applies/{studyApplyId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptApply(
            @PathVariable Long studyApplyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.acceptApply(studyApplyId, userDetails.getUserId()));
    }

    // 거절
    @PatchMapping("/applies/{studyApplyId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectApply(
            @PathVariable Long studyApplyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                studyService.rejectApply(studyApplyId, userDetails.getUserId()));
    }
}