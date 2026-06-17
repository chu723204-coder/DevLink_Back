package com.simplecoding.devlinkback.domain.post.controller;

import com.simplecoding.devlinkback.domain.post.dto.CommentResponseDto;
import com.simplecoding.devlinkback.domain.post.dto.PostResponseDto;
import com.simplecoding.devlinkback.domain.post.entity.Post;
import com.simplecoding.devlinkback.domain.post.service.PostService;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts(
            @RequestParam(required = false) Post.Category category) {
        if (category != null) {
            return ResponseEntity.ok(postService.getPostsByCategory(category));
        }
        return ResponseEntity.ok(postService.getPosts());
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Post.Category category,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.createPost(userDetails.getUserId(), title, content, category));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Post.Category category,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.updatePost(postId, userDetails.getUserId(), title, content, category));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.deletePost(postId, userDetails.getUserId()));
    }

    // 좋아요 토글
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.toggleLike(postId, userDetails.getUserId()));
    }

    // ✅ 댓글 목록 조회 - CommentResponseDto 반환
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    // ✅ 댓글 작성 - CommentResponseDto 반환
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long postId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.createComment(postId, userDetails.getUserId(), content));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.updateComment(commentId, userDetails.getUserId(), content));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                postService.deleteComment(commentId, userDetails.getUserId()));
    }
}