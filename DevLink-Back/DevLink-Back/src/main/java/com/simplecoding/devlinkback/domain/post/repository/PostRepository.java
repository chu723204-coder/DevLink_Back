package com.simplecoding.devlinkback.domain.post.repository;

import com.simplecoding.devlinkback.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제되지 않은 전체 게시글 (최신순)
    List<Post> findByDeleteYnOrderByCreatedAtDesc(String deleteYn);

    // 카테고리별 게시글 (최신순)
    List<Post> findByCategoryAndDeleteYnOrderByCreatedAtDesc(
            Post.Category category, String deleteYn);

    // 단건 조회 (삭제되지 않은)
    Optional<Post> findByPostIdAndDeleteYn(Long postId, String deleteYn);

    // 특정 유저의 게시글 목록 (최신순)
    List<Post> findByUserIdAndDeleteYnOrderByCreatedAtDesc(Long userId, String deleteYn);

    // ✅ 관리자 - 통계용
    long countByDeleteYn(String deleteYn);
}