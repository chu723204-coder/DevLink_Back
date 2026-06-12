package com.simplecoding.devlinkback.domain.study.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "studies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private String techStacks;

    @Column(nullable = false)
    private Integer maxMembers;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentMembers = 1;

    @Column
    private String deadline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.OPEN;

    @Column(nullable = false)
    @Builder.Default
    private String deleteYn = "N";

    // 스터디 수정
    public void update(String title, String description, String techStacks,
                       Integer maxMembers, String deadline) {
        this.title = title;
        this.description = description;
        this.techStacks = techStacks;
        this.maxMembers = maxMembers;
        this.deadline = deadline;
    }

    // 모집 마감
    public void close() {
        this.status = Status.CLOSED;
    }

    // 현재 인원 증가
    public void increaseCurrentMembers() {
        this.currentMembers++;
    }

    // 스터디 삭제
    public void delete() {
        this.deleteYn = "Y";
    }

    public enum Status {
        OPEN, CLOSED
    }
}