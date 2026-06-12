package com.simplecoding.devlinkback.domain.post.repository;

import com.simplecoding.devlinkback.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 댓글 목록 (등록순)
    List<Comment> findByPostIdAndDeleteYnOrderByCreatedAtAsc(Long postId, String deleteYn);

    // 단건 조회 (삭제되지 않은)
    Optional<Comment> findByCommentIdAndDeleteYn(Long commentId, String deleteYn);

    // 특정 유저의 댓글 목록
    List<Comment> findByUserIdAndDeleteYnOrderByCreatedAtDesc(Long userId, String deleteYn);

    // 게시글 삭제 시 댓글 수 조회
    long countByPostIdAndDeleteYn(Long postId, String deleteYn);
}