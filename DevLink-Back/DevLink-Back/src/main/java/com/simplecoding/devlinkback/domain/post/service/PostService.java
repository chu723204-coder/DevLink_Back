package com.simplecoding.devlinkback.domain.post.service;

import com.simplecoding.devlinkback.domain.post.entity.Comment;
import com.simplecoding.devlinkback.domain.post.entity.Post;
import com.simplecoding.devlinkback.domain.post.entity.PostLike;
import com.simplecoding.devlinkback.domain.post.repository.CommentRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostLikeRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    // ── 게시글 ────────────────────────────────────────────────

    // 게시글 전체 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<Post>> getPosts() {
        List<Post> posts = postRepository.findByDeleteYnOrderByCreatedAtDesc("N");
        return ApiResponse.success(posts, "게시글 목록 조회 성공");
    }

    // 카테고리별 게시글 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<Post>> getPostsByCategory(Post.Category category) {
        List<Post> posts = postRepository
                .findByCategoryAndDeleteYnOrderByCreatedAtDesc(category, "N");
        return ApiResponse.success(posts, "카테고리별 게시글 목록 조회 성공");
    }

    // 게시글 상세 조회
    @Transactional
    public ApiResponse<Post> getPost(Long postId) {
        Post post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        post.increaseViewCount();
        return ApiResponse.success(post, "게시글 상세 조회 성공");
    }

    // 게시글 작성
    @Transactional
    public ApiResponse<Post> createPost(Long userId, String title, String content, Post.Category category) {
        Post post = Post.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .category(category)
                .build();
        return ApiResponse.success(postRepository.save(post), "게시글 작성 성공");
    }

    // 게시글 수정
    @Transactional
    public ApiResponse<Post> updatePost(Long postId, Long userId, String title, String content, Post.Category category) {
        Post post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        if (!post.getUserId().equals(userId)) {
            throw CommonException.forbidden("게시글 수정 권한이 없습니다.");
        }
        post.update(title, content, category);
        return ApiResponse.success(post, "게시글 수정 성공");
    }

    // 게시글 삭제
    @Transactional
    public ApiResponse<Void> deletePost(Long postId, Long userId) {
        Post post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        if (!post.getUserId().equals(userId)) {
            throw CommonException.forbidden("게시글 삭제 권한이 없습니다.");
        }
        post.delete();
        return ApiResponse.success(null, "게시글 삭제 성공");
    }

    // ── 좋아요 ────────────────────────────────────────────────

    // 좋아요 토글
    @Transactional
    public ApiResponse<Void> toggleLike(Long postId, Long userId) {
        postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            PostLike like = postLikeRepository.findByPostIdAndUserId(postId, userId)
                    .orElseThrow(() -> CommonException.notFound("좋아요 정보를 찾을 수 없습니다."));
            postLikeRepository.delete(like);
            return ApiResponse.success(null, "좋아요 취소 성공");
        } else {
            PostLike like = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            postLikeRepository.save(like);
            return ApiResponse.success(null, "좋아요 성공");
        }
    }

    // ── 댓글 ──────────────────────────────────────────────────

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<Comment>> getComments(Long postId) {
        List<Comment> comments = commentRepository
                .findByPostIdAndDeleteYnOrderByCreatedAtAsc(postId, "N");
        return ApiResponse.success(comments, "댓글 목록 조회 성공");
    }

    // 댓글 작성
    @Transactional
    public ApiResponse<Comment> createComment(Long postId, Long userId, String content) {
        postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();
        return ApiResponse.success(commentRepository.save(comment), "댓글 작성 성공");
    }

    // 댓글 수정
    @Transactional
    public ApiResponse<Comment> updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findByCommentIdAndDeleteYn(commentId, "N")
                .orElseThrow(() -> CommonException.notFound("댓글을 찾을 수 없습니다."));
        if (!comment.getUserId().equals(userId)) {
            throw CommonException.forbidden("댓글 수정 권한이 없습니다.");
        }
        comment.update(content);
        return ApiResponse.success(comment, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public ApiResponse<Void> deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findByCommentIdAndDeleteYn(commentId, "N")
                .orElseThrow(() -> CommonException.notFound("댓글을 찾을 수 없습니다."));
        if (!comment.getUserId().equals(userId)) {
            throw CommonException.forbidden("댓글 삭제 권한이 없습니다.");
        }
        comment.delete();
        return ApiResponse.success(null, "댓글 삭제 성공");
    }
}