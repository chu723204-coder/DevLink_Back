package com.simplecoding.devlinkback.domain.study.dto;

import com.simplecoding.devlinkback.domain.study.entity.Study;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyResponseDto {

    private Long studyId;
    private String title;
    private String description;
    private String techStacks;
    private Integer maxMembers;
    private Integer currentMembers;
    private String deadline;
    private Study.Status status;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}