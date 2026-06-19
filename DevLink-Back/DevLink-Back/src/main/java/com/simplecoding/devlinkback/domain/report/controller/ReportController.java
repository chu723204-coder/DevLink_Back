package com.simplecoding.devlinkback.domain.report.controller;

import com.simplecoding.devlinkback.domain.report.dto.ReportRequestDto;
import com.simplecoding.devlinkback.domain.report.dto.ReportResponseDto;
import com.simplecoding.devlinkback.domain.report.dto.ReportStatusUpdateDto;
import com.simplecoding.devlinkback.domain.report.service.ReportService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 신고 접수 (로그인 유저)
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<Void>> createReport(@RequestBody ReportRequestDto dto) {
        reportService.createReport(dto);
        return ResponseEntity.ok(ApiResponse.success(null, "신고가 접수되었습니다."));
    }

    // 관리자 - 전체 신고 목록 조회
    @GetMapping("/admin/reports")
    public ResponseEntity<ApiResponse<List<ReportResponseDto>>> getAllReports() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAllReports(), "신고 목록 조회 성공"));
    }

    // 관리자 - 신고 처리 (수락/반려)
    @PatchMapping("/admin/reports/{reportId}")
    public ResponseEntity<ApiResponse<Void>> updateReportStatus(
            @PathVariable Long reportId,
            @RequestBody ReportStatusUpdateDto dto) {
        reportService.updateReportStatus(reportId, dto);
        return ResponseEntity.ok(ApiResponse.success(null, "신고 처리 완료"));
    }
}