package com.dat_viet_group.datvietgroup.modules.social.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowStatsResponse;
import com.dat_viet_group.datvietgroup.modules.social.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /** Bật/tắt theo dõi user {userId}, không cần body */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FollowStatsResponse>> toggleFollow(
            Authentication authentication,
            @PathVariable long userId) {
        FollowStatsResponse result = followService.toggleFollow(authentication.getName(), userId);
        return ApiResponse.ok(result.isFollowing() ? "Đã theo dõi" : "Đã bỏ theo dõi", result);
    }

    /** Danh sách người đang theo dõi user {userId}, mới nhất trước */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<FollowResponse>>> getFollowers(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách người theo dõi thành công",
                followService.getFollowers(userId, pageable(page, size)));
    }

    /** Danh sách người mà user {userId} đang theo dõi, mới nhất trước */
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Page<FollowResponse>>> getFollowing(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách đang theo dõi thành công",
                followService.getFollowing(userId, pageable(page, size)));
    }

    /** Số follower, số following của user {userId} và mình có đang theo dõi họ không */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<ApiResponse<FollowStatsResponse>> getStats(
            Authentication authentication,
            @PathVariable long userId) {
        return ApiResponse.ok("Lấy thống kê theo dõi thành công",
                followService.getStats(authentication.getName(), userId));
    }

    private PageRequest pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
