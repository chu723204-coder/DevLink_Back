package com.simplecoding.devlinkback.domain.user.service;

import com.simplecoding.devlinkback.domain.post.dto.PostResponseDto;
import com.simplecoding.devlinkback.domain.post.repository.CommentRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostLikeRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostRepository;
import com.simplecoding.devlinkback.domain.study.dto.StudyResponseDto;
import com.simplecoding.devlinkback.domain.study.repository.StudyRepository;
import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final StudyRepository studyRepository;
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public ApiResponse<User> getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        return ApiResponse.success(user, "내 정보 조회 성공");
    }

    // 닉네임 수정
    @Transactional
    public ApiResponse<User> updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        user.updateNickname(nickname);
        return ApiResponse.success(user, "닉네임 수정 성공");
    }

    // 비밀번호 수정
    @Transactional
    public ApiResponse<Void> updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw CommonException.badRequest("현재 비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
        return ApiResponse.success(null, "비밀번호 수정 성공");
    }

    // 회원 탈퇴 - ✅ 이메일 익명화 처리 (재가입 허용)
    @Transactional
    public ApiResponse<Void> deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));

        // 이메일 익명화: deleted_{userId}_{원래이메일}
        String anonymizedEmail = "deleted_" + userId + "_" + user.getEmail();
        // 닉네임 익명화: deleted_{userId} (닉네임도 UNIQUE라 변경 필요)
        String anonymizedNickname = "deleted_" + userId;

        user.anonymize(anonymizedEmail, anonymizedNickname);
        user.delete();

        return ApiResponse.success(null, "회원 탈퇴 성공");
    }

    // 내 게시글 목록
    @Transactional(readOnly = true)
    public ApiResponse<List<PostResponseDto>> getMyPosts(Long userId) {
        String nickname = userRepository.findById(userId)
                .map(User::getNickname).orElse("알 수 없음");

        List<PostResponseDto> posts = postRepository
                .findByUserIdAndDeleteYnOrderByCreatedAtDesc(userId, "N")
                .stream()
                .map(post -> PostResponseDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .category(post.getCategory())
                        .nickname(nickname)
                        .viewCount(post.getViewCount())
                        .likeCount((int) postLikeRepository.countByPostId(post.getPostId()))
                        .commentCount((int) commentRepository.countByPostIdAndDeleteYn(post.getPostId(), "N"))
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.success(posts, "내 게시글 목록 조회 성공");
    }

    // 내 스터디 목록
    @Transactional(readOnly = true)
    public ApiResponse<List<StudyResponseDto>> getMyStudies(Long userId) {
        String nickname = userRepository.findById(userId)
                .map(User::getNickname).orElse("알 수 없음");

        List<StudyResponseDto> studies = studyRepository
                .findByUserIdAndDeleteYnOrderByCreatedAtDesc(userId, "N")
                .stream()
                .map(study -> StudyResponseDto.builder()
                        .studyId(study.getStudyId())
                        .title(study.getTitle())
                        .description(study.getDescription())
                        .techStacks(study.getTechStacks())
                        .maxMembers(study.getMaxMembers())
                        .currentMembers(study.getCurrentMembers())
                        .deadline(study.getDeadline())
                        .status(study.getStatus())
                        .nickname(nickname)
                        .createdAt(study.getCreatedAt())
                        .updatedAt(study.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.success(studies, "내 스터디 목록 조회 성공");
    }
}