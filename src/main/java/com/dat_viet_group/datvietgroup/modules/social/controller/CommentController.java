package com.dat_viet_group.datvietgroup.modules.social.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.request.CommentCreateRequest;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.CommentResponse;
import com.dat_viet_group.datvietgroup.modules.social.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** JSON: { "postId": 1, "parentId": null, "content": "..." } — parentId có thì là trả lời bình luận */
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            Authentication authentication,
            @Valid @RequestBody CommentCreateRequest request) {
        return ApiResponse.created("Bình luận thành công",
                commentService.createComment(authentication.getName(), request));
    }

    /** Bình luận gốc của bài viết, cũ nhất trước */
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByPost(
            @PathVariable long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy bình luận thành công",
                commentService.getCommentsByPost(postId, pageable(page, size)));
    }

    /** Các trả lời của một bình luận */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getReplies(
            @PathVariable long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy trả lời thành công",
                commentService.getReplies(commentId, pageable(page, size)));
    }

    private PageRequest pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.ASC, "createdAt"));
    }
}
