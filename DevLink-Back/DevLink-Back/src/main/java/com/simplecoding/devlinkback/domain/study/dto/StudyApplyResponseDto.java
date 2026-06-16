package com.simplecoding.devlinkback.domain.study.dto;

import com.simplecoding.devlinkback.domain.study.entity.StudyApply;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyApplyResponseDto {
    private Long studyApplyId;
    private Long studyId;
    private Long userId;
    private String nickname;
    private StudyApply.Status status;
}