package com.simplecoding.devlinkback.domain.study.repository;

import com.simplecoding.devlinkback.domain.study.entity.StudyApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyApplyRepository extends JpaRepository<StudyApply, Long> {

    // 스터디의 전체 지원자 목록
    List<StudyApply> findByStudyId(Long studyId);

    // 스터디의 특정 상태 지원자 목록
    List<StudyApply> findByStudyIdAndStatus(Long studyId, StudyApply.Status status);

    // 중복 지원 확인
    boolean existsByStudyIdAndUserId(Long studyId, Long userId);

    // 단건 조회 (수락/거절 처리용)
    Optional<StudyApply> findByStudyApplyId(Long studyApplyId);

    // 특정 유저가 지원한 스터디 목록
    List<StudyApply> findByUserIdOrderByCreatedAtDesc(Long userId);
}