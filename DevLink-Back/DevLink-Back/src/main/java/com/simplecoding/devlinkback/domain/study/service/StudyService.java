package com.simplecoding.devlinkback.domain.study.service;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.entity.ChatRoomMember;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomMemberRepository;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomRepository;
import com.simplecoding.devlinkback.domain.notification.entity.Notification;
import com.simplecoding.devlinkback.domain.notification.service.NotificationService;
import com.simplecoding.devlinkback.domain.study.dto.StudyApplyResponseDto;
import com.simplecoding.devlinkback.domain.study.dto.StudyResponseDto;
import com.simplecoding.devlinkback.domain.study.entity.Study;
import com.simplecoding.devlinkback.domain.study.entity.StudyApply;
import com.simplecoding.devlinkback.domain.study.repository.StudyApplyRepository;
import com.simplecoding.devlinkback.domain.study.repository.StudyRepository;
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
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyApplyRepository studyApplyRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private StudyResponseDto toDto(Study study) {
        String nickname = userRepository.findById(study.getUserId())
                .map(u -> u.getNickname())
                .orElse("알 수 없음");

        return StudyResponseDto.builder()
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
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<StudyResponseDto>> getStudies() {
        List<StudyResponseDto> studies = studyRepository.findByDeleteYnOrderByCreatedAtDesc("N")
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success(studies, "스터디 목록 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<StudyResponseDto>> getOpenStudies() {
        List<StudyResponseDto> studies = studyRepository
                .findByStatusAndDeleteYnOrderByCreatedAtDesc(Study.Status.OPEN, "N")
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success(studies, "모집 중인 스터디 목록 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<StudyResponseDto> getStudy(Long studyId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        return ApiResponse.success(toDto(study), "스터디 상세 조회 성공");
    }

    @Transactional
    public ApiResponse<StudyResponseDto> createStudy(Long userId, String title, String description,
                                                     String techStacks, Integer maxMembers, String deadline) {
        Study study = Study.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .techStacks(techStacks)
                .maxMembers(maxMembers)
                .deadline(deadline)
                .build();
        return ApiResponse.success(toDto(studyRepository.save(study)), "스터디 등록 성공");
    }

    @Transactional
    public ApiResponse<StudyResponseDto> updateStudy(Long studyId, Long userId, String title,
                                                     String description, String techStacks,
                                                     Integer maxMembers, String deadline) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("스터디 수정 권한이 없습니다.");
        }
        study.update(title, description, techStacks, maxMembers, deadline);
        return ApiResponse.success(toDto(study), "스터디 수정 성공");
    }

    @Transactional
    public ApiResponse<Void> deleteStudy(Long studyId, Long userId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("스터디 삭제 권한이 없습니다.");
        }
        study.delete();

        chatRoomRepository.findByStudyId(studyId).ifPresent(chatRoom -> {
            chatRoomMemberRepository.deleteByChatRoom_ChatRoomId(chatRoom.getChatRoomId());
            chatRoomRepository.delete(chatRoom);
        });

        return ApiResponse.success(null, "스터디 삭제 성공");
    }

    @Transactional
    public ApiResponse<Void> closeStudy(Long studyId, Long userId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("스터디 마감 권한이 없습니다.");
        }
        study.close();
        return ApiResponse.success(null, "모집 마감 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<StudyApplyResponseDto>> getApplies(Long studyId) {
        List<StudyApplyResponseDto> applies = studyApplyRepository.findByStudyId(studyId)
                .stream()
                .map(apply -> {
                    String nickname = userRepository.findById(apply.getUserId())
                            .map(u -> u.getNickname())
                            .orElse("알 수 없음");
                    return StudyApplyResponseDto.builder()
                            .studyApplyId(apply.getStudyApplyId())
                            .studyId(apply.getStudyId())
                            .userId(apply.getUserId())
                            .nickname(nickname)
                            .status(apply.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
        return ApiResponse.success(applies, "지원자 목록 조회 성공");
    }

    @Transactional
    public ApiResponse<StudyApply> apply(Long studyId, Long userId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (study.getStatus() == Study.Status.CLOSED) {
            throw CommonException.badRequest("모집이 마감된 스터디입니다.");
        }
        if (studyApplyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw CommonException.badRequest("이미 지원한 스터디입니다.");
        }
        StudyApply apply = StudyApply.builder()
                .studyId(studyId)
                .userId(userId)
                .build();
        StudyApply saved = studyApplyRepository.save(apply);

        // 스터디 모집자에게 지원 알림
        String applicantNickname = userRepository.findById(userId)
                .map(u -> u.getNickname()).orElse("누군가");
        notificationService.sendNotification(
                study.getUserId(),
                Notification.NotificationType.STUDY_APPLY,
                applicantNickname + "님이 '" + study.getTitle() + "' 스터디에 지원했습니다.",
                "/studies/" + studyId
        );

        return ApiResponse.success(saved, "지원 성공");
    }

    @Transactional
    public ApiResponse<Void> acceptApply(Long studyApplyId, Long userId) {
        StudyApply apply = studyApplyRepository.findByStudyApplyId(studyApplyId)
                .orElseThrow(() -> CommonException.notFound("지원 정보를 찾을 수 없습니다."));
        Study study = studyRepository.findByStudyIdAndDeleteYn(apply.getStudyId(), "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("수락 권한이 없습니다.");
        }
        apply.accept();
        study.increaseCurrentMembers();

        ChatRoom chatRoom = chatRoomRepository.findByStudyId(study.getStudyId())
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .studyId(study.getStudyId())
                            .roomName(study.getTitle())
                            .build();
                    return chatRoomRepository.save(newRoom);
                });

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> CommonException.notFound("유저를 찾을 수 없습니다."));
        if (!chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(
                chatRoom.getChatRoomId(), userId)) {
            chatRoomMemberRepository.save(ChatRoomMember.builder()
                    .chatRoom(chatRoom).user(owner).build());
        }

        User applicant = userRepository.findById(apply.getUserId())
                .orElseThrow(() -> CommonException.notFound("지원자를 찾을 수 없습니다."));
        if (!chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(
                chatRoom.getChatRoomId(), apply.getUserId())) {
            chatRoomMemberRepository.save(ChatRoomMember.builder()
                    .chatRoom(chatRoom).user(applicant).build());
        }

        // 지원자에게 수락 알림
        notificationService.sendNotification(
                apply.getUserId(),
                Notification.NotificationType.STUDY_ACCEPT,
                "'" + study.getTitle() + "' 스터디 지원이 수락되었습니다.",
                "/studies/" + study.getStudyId()
        );

        return ApiResponse.success(null, "지원 수락 성공");
    }

    @Transactional
    public ApiResponse<Void> rejectApply(Long studyApplyId, Long userId) {
        StudyApply apply = studyApplyRepository.findByStudyApplyId(studyApplyId)
                .orElseThrow(() -> CommonException.notFound("지원 정보를 찾을 수 없습니다."));
        Study study = studyRepository.findByStudyIdAndDeleteYn(apply.getStudyId(), "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("거절 권한이 없습니다.");
        }
        apply.reject();

        // 지원자에게 거절 알림
        notificationService.sendNotification(
                apply.getUserId(),
                Notification.NotificationType.STUDY_REJECT,
                "'" + study.getTitle() + "' 스터디 지원이 거절되었습니다.",
                "/studies/" + study.getStudyId()
        );

        return ApiResponse.success(null, "지원 거절 성공");
    }
}