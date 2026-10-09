package com.dat_viet_group.datvietgroup.modules.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationTypeResponse;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationTypeService;

import lombok.RequiredArgsConstructor;

/** Admin quản lý loại thông báo. Quyền ADMIN do SecurityConfig kiểm tra qua đường dẫn /api/admin/**. */
@RestController
@RequestMapping("/api/admin/notification-types")
@RequiredArgsConstructor
public class AdminNotificationTypeController {

    private final NotificationTypeService notificationTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationTypeResponse>>> getAll() {
        return ApiResponse.ok("Lấy danh sách loại thông báo thành công", notificationTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTypeResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết loại thông báo thành công", notificationTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationTypeResponse>> create(
            @Validated @RequestBody CreateNotificationTypeRequest request) {
        return ApiResponse.created("Tạo loại thông báo thành công", notificationTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTypeResponse>> update(@PathVariable Long id,
            @Validated @RequestBody UpdateNotificationTypeRequest request) {
        return ApiResponse.ok("Cập nhật loại thông báo thành công", notificationTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationTypeService.delete(id);
        return ApiResponse.ok("Xóa loại thông báo thành công");
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<NotificationTypeResponse>> toggle(@PathVariable Long id) {
        return ApiResponse.ok("Đổi trạng thái ẩn/hiện thành công", notificationTypeService.toggle(id));
    }
}