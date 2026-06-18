package com.simplecoding.devlinkback.domain.study.repository;

import com.simplecoding.devlinkback.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    // 삭제되지 않은 전체 스터디 목록 (최신순)
    List<Study> findByDeleteYnOrderByCreatedAtDesc(String deleteYn);

    // 모집 중인 스터디 목록 (최신순)
    List<Study> findByStatusAndDeleteYnOrderByCreatedAtDesc(
            Study.Status status, String deleteYn);

    // 단건 조회 (삭제되지 않은)
    Optional<Study> findByStudyIdAndDeleteYn(Long studyId, String deleteYn);

    // 특정 유저의 스터디 목록 (최신순)
    List<Study> findByUserIdAndDeleteYnOrderByCreatedAtDesc(Long userId, String deleteYn);

    // ✅ 관리자 - 통계용
    long countByDeleteYn(String deleteYn);
}