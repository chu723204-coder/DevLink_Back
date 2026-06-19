package com.simplecoding.devlinkback.domain.report.dto;

import com.simplecoding.devlinkback.domain.report.entity.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponseDto {

    private Long reportId;
    private Long reporterId;
    private Report.TargetType targetType;
    private Long targetId;
    private Report.ReportReason reason;
    private String etcReason;
    private Report.ReportStatus status;
    private LocalDateTime createdAt;

    public static ReportResponseDto from(Report report) {
        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporterId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .etcReason(report.getEtcReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}