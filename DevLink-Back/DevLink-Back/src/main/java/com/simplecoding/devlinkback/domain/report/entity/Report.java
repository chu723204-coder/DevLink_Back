package com.simplecoding.devlinkback.domain.report.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    // 신고한 유저
    @Column(nullable = false)
    private Long reporterId;

    // 신고 대상 타입 (POST / COMMENT)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    // 신고 대상 ID (postId or commentId)
    @Column(nullable = false)
    private Long targetId;

    // 신고 사유
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    // 기타 사유 (reason이 OTHER일 때만 사용)
    @Column(nullable = true)
    private String etcReason;

    // 처리 상태
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    // 처리 상태 변경
    public void updateStatus(ReportStatus status) {
        this.status = status;
    }

    public enum TargetType {
        POST, COMMENT
    }

    public enum ReportReason {
        SPAM,           // 스팸/도배
        OBSCENE,        // 음란/선정성
        ABUSE,          // 욕설/비방
        ILLEGAL,        // 불법 정보
        OTHER           // 기타
    }

    public enum ReportStatus {
        PENDING,    // 처리 대기
        ACCEPTED,   // 처리 완료 (삭제/정지)
        REJECTED    // 반려
    }
}