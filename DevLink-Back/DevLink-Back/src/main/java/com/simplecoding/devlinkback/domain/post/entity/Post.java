package com.simplecoding.devlinkback.domain.post.entity;

import com.simplecoding.devlinkback.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private String deleteYn = "N";

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 게시글 수정
    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // 게시글 삭제
    public void delete() {
        this.deleteYn = "Y";
    }

    public enum Category {
        FREE, INTERVIEW, TECH, JOB
    }
}