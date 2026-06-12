package com.simplecoding.devlinkback.domain.post.repository;

import com.simplecoding.devlinkback.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 좋아요 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 단건 조회 (취소용)
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 게시글 좋아요 수
    long countByPostId(Long postId);
}