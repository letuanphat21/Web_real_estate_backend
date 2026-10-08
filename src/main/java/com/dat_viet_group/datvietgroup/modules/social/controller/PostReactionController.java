package com.dat_viet_group.datvietgroup.modules.social.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostReactionResponse;
import com.dat_viet_group.datvietgroup.modules.social.service.PostReactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/posts/{postId}/reactions")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    /** Bật/tắt thích bài viết, không cần body */
    @PostMapping
    public ResponseEntity<ApiResponse<PostReactionResponse>> toggleLike(
            Authentication authentication,
            @PathVariable long postId) {
        PostReactionResponse result = postReactionService.toggleLike(authentication.getName(), postId);
        return ApiResponse.ok(result.isLiked() ? "Đã thích bài viết" : "Đã bỏ thích bài viết", result);
    }

    /** Tổng lượt thích và trạng thái thích của người dùng hiện tại */
    @GetMapping
    public ResponseEntity<ApiResponse<PostReactionResponse>> getReaction(
            Authentication authentication,
            @PathVariable long postId) {
        return ApiResponse.ok("Lấy lượt thích thành công",
                postReactionService.getReaction(authentication.getName(), postId));
    }
}
