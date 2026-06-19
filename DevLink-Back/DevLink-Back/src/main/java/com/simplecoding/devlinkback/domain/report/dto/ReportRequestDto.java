package com.simplecoding.devlinkback.domain.report.dto;

import com.simplecoding.devlinkback.domain.report.entity.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {

    private Report.TargetType targetType;  // POST / COMMENT
    private Long targetId;                 // postId or commentId
    private Report.ReportReason reason;    // 신고 사유
    private String etcReason;             // 기타 사유 (reason이 OTHER일 때만 사용)
}