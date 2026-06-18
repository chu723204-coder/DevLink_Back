package com.simplecoding.devlinkback.domain.admin.service;

import com.simplecoding.devlinkback.domain.admin.dto.AdminStatsDto;
import com.simplecoding.devlinkback.domain.post.repository.PostRepository;
import com.simplecoding.devlinkback.domain.study.repository.StudyRepository;
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
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final StudyRepository studyRepository;

    // ── 통계 조회 ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<AdminStatsDto> getStats() {
        long totalUsers = userRepository.countByDeleteYn("N");
        long totalPosts = postRepository.countByDeleteYn("N");
        long totalStudies = studyRepository.countByDeleteYn("N");
        long bannedUsers = userRepository.countByDeleteYnAndBanned("N", true);

        return ApiResponse.success(AdminStatsDto.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalStudies(totalStudies)
                .bannedUsers(bannedUsers)
                .build(), "통계 조회 성공");
    }

    // ── 회원 목록 조회 ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public ApiResponse<List<User>> getUsers() {
        List<User> users = userRepository.findByDeleteYnOrderByCreatedAtDesc("N");
        return ApiResponse.success(users, "회원 목록 조회 성공");
    }

    // ── 강제 탈퇴 ──────────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        user.delete();
        return ApiResponse.success(null, "강제 탈퇴 성공");
    }

    // ── 계정 정지/해제 ──────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> toggleBan(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        user.toggleBan();
        String message = user.getBanned() ? "계정 정지 성공" : "계정 정지 해제 성공";
        return ApiResponse.success(null, message);
    }

    // ── 게시글 강제 삭제 ────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> deletePost(Long postId) {
        var post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        post.delete();
        return ApiResponse.success(null, "게시글 강제 삭제 성공");
    }

    // ── 스터디 강제 마감 ────────────────────────────────────────
    @Transactional
    public ApiResponse<Void> closeStudy(Long studyId) {
        var study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        study.close();
        return ApiResponse.success(null, "스터디 강제 마감 성공");
    }
}