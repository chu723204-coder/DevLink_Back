package com.simplecoding.devlinkback.domain.study.service;

import com.simplecoding.devlinkback.domain.chat.entity.ChatRoom;
import com.simplecoding.devlinkback.domain.chat.repository.ChatRoomRepository;
import com.simplecoding.devlinkback.domain.study.entity.Study;
import com.simplecoding.devlinkback.domain.study.entity.StudyApply;
import com.simplecoding.devlinkback.domain.study.repository.StudyApplyRepository;
import com.simplecoding.devlinkback.domain.study.repository.StudyRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyApplyRepository studyApplyRepository;
    private final ChatRoomRepository chatRoomRepository;

    // ── 스터디 ────────────────────────────────────────────────

    // 스터디 전체 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<Study>> getStudies() {
        List<Study> studies = studyRepository.findByDeleteYnOrderByCreatedAtDesc("N");
        return ApiResponse.success(studies, "스터디 목록 조회 성공");
    }

    // 모집 중인 스터디 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<Study>> getOpenStudies() {
        List<Study> studies = studyRepository
                .findByStatusAndDeleteYnOrderByCreatedAtDesc(Study.Status.OPEN, "N");
        return ApiResponse.success(studies, "모집 중인 스터디 목록 조회 성공");
    }

    // 스터디 상세 조회
    @Transactional(readOnly = true)
    public ApiResponse<Study> getStudy(Long studyId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        return ApiResponse.success(study, "스터디 상세 조회 성공");
    }

    // 스터디 등록
    @Transactional
    public ApiResponse<Study> createStudy(Long userId, String title, String description,
                                          String techStacks, Integer maxMembers, String deadline) {
        Study study = Study.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .techStacks(techStacks)
                .maxMembers(maxMembers)
                .deadline(deadline)
                .build();
        return ApiResponse.success(studyRepository.save(study), "스터디 등록 성공");
    }

    // 스터디 수정
    @Transactional
    public ApiResponse<Study> updateStudy(Long studyId, Long userId, String title,
                                          String description, String techStacks,
                                          Integer maxMembers, String deadline) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("스터디 수정 권한이 없습니다.");
        }
        study.update(title, description, techStacks, maxMembers, deadline);
        return ApiResponse.success(study, "스터디 수정 성공");
    }

    // 스터디 삭제
    @Transactional
    public ApiResponse<Void> deleteStudy(Long studyId, Long userId) {
        Study study = studyRepository.findByStudyIdAndDeleteYn(studyId, "N")
                .orElseThrow(() -> CommonException.notFound("스터디를 찾을 수 없습니다."));
        if (!study.getUserId().equals(userId)) {
            throw CommonException.forbidden("스터디 삭제 권한이 없습니다.");
        }
        study.delete();
        return ApiResponse.success(null, "스터디 삭제 성공");
    }

    // 모집 마감
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

    // ── 지원 ──────────────────────────────────────────────────

    // 지원자 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<StudyApply>> getApplies(Long studyId) {
        List<StudyApply> applies = studyApplyRepository.findByStudyId(studyId);
        return ApiResponse.success(applies, "지원자 목록 조회 성공");
    }

    // 지원하기
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
        return ApiResponse.success(studyApplyRepository.save(apply), "지원 성공");
    }

    // 수락
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

        // 채팅방 자동 생성 (없는 경우에만)
        if (chatRoomRepository.findByStudyId(study.getStudyId()).isEmpty()) {
            ChatRoom chatRoom = ChatRoom.builder()
                    .studyId(study.getStudyId())
                    .roomName(study.getTitle())
                    .build();
            chatRoomRepository.save(chatRoom);
        }

        return ApiResponse.success(null, "지원 수락 성공");
    }

    // 거절
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
        return ApiResponse.success(null, "지원 거절 성공");
    }
}