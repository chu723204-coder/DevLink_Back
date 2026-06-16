package com.simplecoding.devlinkback.domain.post.dto;

import com.simplecoding.devlinkback.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {

    private Long postId;
    private String title;
    private String content;
    private Post.Category category;
    private String nickname;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}