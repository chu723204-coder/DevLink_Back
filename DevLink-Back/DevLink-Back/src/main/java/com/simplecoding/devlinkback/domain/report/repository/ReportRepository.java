package com.simplecoding.devlinkback.domain.report.repository;

import com.simplecoding.devlinkback.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 중복 신고 방지 체크
    boolean existsByReporterIdAndTargetTypeAndTargetId(
            Long reporterId,
            Report.TargetType targetType,
            Long targetId
    );

    // 관리자 - 전체 신고 목록 (최신순)
    List<Report> findAllByOrderByCreatedAtDesc();

    // 관리자 - 상태별 신고 목록
    List<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status);
}