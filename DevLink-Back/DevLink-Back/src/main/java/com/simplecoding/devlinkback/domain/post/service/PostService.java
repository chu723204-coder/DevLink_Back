package com.simplecoding.devlinkback.domain.post.service;

import com.simplecoding.devlinkback.domain.post.dto.CommentResponseDto;
import com.simplecoding.devlinkback.domain.post.dto.PostResponseDto;
import com.simplecoding.devlinkback.domain.post.entity.Comment;
import com.simplecoding.devlinkback.domain.post.entity.Post;
import com.simplecoding.devlinkback.domain.post.entity.PostLike;
import com.simplecoding.devlinkback.domain.post.repository.CommentRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostLikeRepository;
import com.simplecoding.devlinkback.domain.post.repository.PostRepository;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import com.simplecoding.devlinkback.global.common.ApiResponse;
import com.simplecoding.devlinkback.global.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    // Post → PostResponseDto 변환
    private PostResponseDto toDto(Post post) {
        String nickname = userRepository.findById(post.getUserId())
                .map(u -> u.getNickname())
                .orElse("알 수 없음");
        int likeCount = (int) postLikeRepository.countByPostId(post.getPostId());
        int commentCount = (int) commentRepository.countByPostIdAndDeleteYn(post.getPostId(), "N");

        return PostResponseDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .nickname(nickname)
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // Comment → CommentResponseDto 변환
    private CommentResponseDto toCommentDto(Comment comment) {
        String nickname = userRepository.findById(comment.getUserId())
                .map(u -> u.getNickname())
                .orElse("알 수 없음");
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .nickname(nickname)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    // 게시글 전체 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<PostResponseDto>> getPosts() {
        List<PostResponseDto> posts = postRepository.findByDeleteYnOrderByCreatedAtDesc("N")
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success(posts, "게시글 목록 조회 성공");
    }

    // 카테고리별 게시글 목록 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<PostResponseDto>> getPostsByCategory(Post.Category category) {
        List<PostResponseDto> posts = postRepository
                .findByCategoryAndDeleteYnOrderByCreatedAtDesc(category, "N")
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success(posts, "카테고리별 게시글 목록 조회 성공");
    }

    // 게시글 상세 조회
    @Transactional
    public ApiResponse<PostResponseDto> getPost(Long postId) {
        Post post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        post.increaseViewCount();
        return ApiResponse.success(toDto(post), "게시글 상세 조회 성공");
    }

    // 게시글 작성
    @Transactional
    public ApiResponse<PostResponseDto> createPost(Long userId, String title, String content, Post.Category category) {
        Post post = Post.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .category(category)
                .build();
        return ApiResponse.success(toDto(postRepository.save(post)), "게시글 작성 성공");
    }

    // 게시글 수정
    @Transactional
    public ApiResponse<PostResponseDto> updatePost(Long postId, Long userId, String title, String content, Post.Category category) {
        Post post = postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        if (!post.getUserId().equals(userId)) {
            throw CommonException.forbidden("게시글 수정 권한이 없습니다.");
        }
        post.update(title, content, category);
        return ApiResponse.success(toDto(post), "게시글 수정 성공");
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

    // ✅ 댓글 목록 조회 - nickname 포함 DTO 반환
    @Transactional(readOnly = true)
    public ApiResponse<List<CommentResponseDto>> getComments(Long postId) {
        List<CommentResponseDto> comments = commentRepository
                .findByPostIdAndDeleteYnOrderByCreatedAtAsc(postId, "N")
                .stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
        return ApiResponse.success(comments, "댓글 목록 조회 성공");
    }

    // ✅ 댓글 작성 - nickname 포함 DTO 반환
    @Transactional
    public ApiResponse<CommentResponseDto> createComment(Long postId, Long userId, String content) {
        postRepository.findByPostIdAndDeleteYn(postId, "N")
                .orElseThrow(() -> CommonException.notFound("게시글을 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();
        Comment saved = commentRepository.save(comment);
        return ApiResponse.success(toCommentDto(saved), "댓글 작성 성공");
    }

    // 댓글 수정
    @Transactional
    public ApiResponse<CommentResponseDto> updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findByCommentIdAndDeleteYn(commentId, "N")
                .orElseThrow(() -> CommonException.notFound("댓글을 찾을 수 없습니다."));
        if (!comment.getUserId().equals(userId)) {
            throw CommonException.forbidden("댓글 수정 권한이 없습니다.");
        }
        comment.update(content);
        return ApiResponse.success(toCommentDto(comment), "댓글 수정 성공");
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