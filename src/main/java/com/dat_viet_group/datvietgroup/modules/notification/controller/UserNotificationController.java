package com.dat_viet_group.datvietgroup.modules.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UnreadCountResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UserNotificationResponse;
import com.dat_viet_group.datvietgroup.modules.notification.service.UserNotificationService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** Thông báo của người dùng đang đăng nhập (chuông thông báo). */
@RestController
@RequestMapping("/api/me/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;
    private final UserService userService;

    /** ?typeId= để lọc theo tab, bỏ trống là lấy tất cả. */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserNotificationResponse>>> getMine(
            @RequestParam(required = false) Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ApiResponse.ok("Lấy danh sách thông báo thành công",
                userNotificationService.getMine(currentUserId(authentication), typeId, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(Authentication authentication) {
        return ApiResponse.ok("Lấy số thông báo chưa đọc thành công",
                userNotificationService.getUnreadCount(currentUserId(authentication)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id, Authentication authentication) {
        userNotificationService.markRead(currentUserId(authentication), id);
        return ApiResponse.ok("Đã đánh dấu đã đọc");
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(Authentication authentication) {
        userNotificationService.markAllRead(currentUserId(authentication));
        return ApiResponse.ok("Đã đánh dấu đã đọc tất cả");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication authentication) {
        userNotificationService.delete(currentUserId(authentication), id);
        return ApiResponse.ok("Đã xóa thông báo");
    }

    // authentication.getName() là email của người đăng nhập (do JwtAuthFilter đặt vào)
    private Long currentUserId(Authentication authentication) {
        return userService.findByEmailOrPhone(authentication.getName()).getId();
    }
}