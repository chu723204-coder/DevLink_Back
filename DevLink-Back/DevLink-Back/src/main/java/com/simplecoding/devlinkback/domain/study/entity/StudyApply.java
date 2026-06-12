package com.simplecoding.devlinkback.domain.study.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "study_applies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"study_id", "user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudyApply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyApplyId;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    // 수락
    public void accept() {
        this.status = Status.ACCEPTED;
    }

    // 거절
    public void reject() {
        this.status = Status.REJECTED;
    }

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}