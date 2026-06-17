package com.simplecoding.devlinkback.domain.notice.service;

import com.simplecoding.devlinkback.domain.notice.dto.NoticeResponseDto;
import com.simplecoding.devlinkback.domain.notice.entity.Notice;
import com.simplecoding.devlinkback.domain.notice.repository.NoticeRepository;
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
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    private NoticeResponseDto toDto(Notice notice) {
        String nickname = userRepository.findById(notice.getUserId())
                .map(u -> u.getNickname()).orElse("관리자");
        return NoticeResponseDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .nickname(nickname)
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<NoticeResponseDto>> getNotices() {
        List<NoticeResponseDto> notices = noticeRepository
                .findByDeleteYnOrderByCreatedAtDesc("N")
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success(notices, "공지사항 목록 조회 성공");
    }

    // 공지사항 상세 조회
    @Transactional(readOnly = true)
    public ApiResponse<NoticeResponseDto> getNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeIdAndDeleteYn(noticeId, "N")
                .orElseThrow(() -> CommonException.notFound("공지사항을 찾을 수 없습니다."));
        return ApiResponse.success(toDto(notice), "공지사항 상세 조회 성공");
    }

    // 공지사항 작성 (관리자)
    @Transactional
    public ApiResponse<NoticeResponseDto> createNotice(Long userId, String title, String content) {
        Notice notice = Notice.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .build();
        return ApiResponse.success(toDto(noticeRepository.save(notice)), "공지사항 작성 성공");
    }

    // 공지사항 수정 (관리자)
    @Transactional
    public ApiResponse<NoticeResponseDto> updateNotice(Long noticeId, String title, String content) {
        Notice notice = noticeRepository.findByNoticeIdAndDeleteYn(noticeId, "N")
                .orElseThrow(() -> CommonException.notFound("공지사항을 찾을 수 없습니다."));
        notice.update(title, content);
        return ApiResponse.success(toDto(notice), "공지사항 수정 성공");
    }

    // 공지사항 삭제 (관리자)
    @Transactional
    public ApiResponse<Void> deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeIdAndDeleteYn(noticeId, "N")
                .orElseThrow(() -> CommonException.notFound("공지사항을 찾을 수 없습니다."));
        notice.delete();
        return ApiResponse.success(null, "공지사항 삭제 성공");
    }
}