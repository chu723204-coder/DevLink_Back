package com.simplecoding.devlinkback.domain.report.service;

import com.simplecoding.devlinkback.domain.report.dto.ReportRequestDto;
import com.simplecoding.devlinkback.domain.report.dto.ReportResponseDto;
import com.simplecoding.devlinkback.domain.report.dto.ReportStatusUpdateDto;
import com.simplecoding.devlinkback.domain.report.entity.Report;
import com.simplecoding.devlinkback.domain.report.repository.ReportRepository;
import com.simplecoding.devlinkback.global.common.CommonException;
import com.simplecoding.devlinkback.global.common.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    // 신고 접수
    @Transactional
    public void createReport(ReportRequestDto dto) {
        Long reporterId = SecurityUtil.getCurrentUserId();

        // 중복 신고 방지
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                reporterId, dto.getTargetType(), dto.getTargetId())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "이미 신고한 대상입니다.");
        }

        // 기타 선택 시 etcReason 필수 체크
        if (dto.getReason() == Report.ReportReason.OTHER &&
                (dto.getEtcReason() == null || dto.getEtcReason().isBlank())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "기타 사유를 입력해 주세요.");
        }

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetType(dto.getTargetType())
                .targetId(dto.getTargetId())
                .reason(dto.getReason())
                .etcReason(dto.getReason() == Report.ReportReason.OTHER ? dto.getEtcReason() : null)
                .build();

        reportRepository.save(report);
    }

    // 관리자 - 전체 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());
    }

    // 관리자 - 신고 처리 (수락/반려)
    @Transactional
    public void updateReportStatus(Long reportId, ReportStatusUpdateDto dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다."));

        report.updateStatus(dto.getStatus());
    }
}