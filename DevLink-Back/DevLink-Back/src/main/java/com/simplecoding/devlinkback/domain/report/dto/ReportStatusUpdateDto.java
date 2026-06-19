package com.simplecoding.devlinkback.domain.report.dto;

import com.simplecoding.devlinkback.domain.report.entity.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportStatusUpdateDto {

    private Report.ReportStatus status;  // ACCEPTED / REJECTED
}