package com.dat_viet_group.datvietgroup.modules.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationResponse;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** Admin soạn và quản lý thông báo. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getAll(
            @RequestParam(required = false) Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách thông báo thành công", notificationService.getAll(typeId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết thông báo thành công", notificationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @Validated @RequestBody CreateNotificationRequest request, Authentication authentication) {
        Long adminId = userService.findByEmailOrPhone(authentication.getName()).getId();
        return ApiResponse.created("Tạo và gửi thông báo thành công", notificationService.create(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> update(@PathVariable Long id,
            @Validated @RequestBody UpdateNotificationRequest request) {
        return ApiResponse.ok("Cập nhật thông báo thành công", notificationService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<NotificationResponse>> toggle(@PathVariable Long id) {
        return ApiResponse.ok("Đổi trạng thái ẩn/hiện thành công", notificationService.toggle(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ApiResponse.ok("Xóa thông báo thành công");
    }
}