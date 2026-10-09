package com.dat_viet_group.datvietgroup.modules.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationTypeResponse;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationTypeService;

import lombok.RequiredArgsConstructor;

/** Phía người dùng: lấy các loại thông báo đang hiện để dựng tab lọc. Cần đăng nhập. */
@RestController
@RequestMapping("/api/notification-types")
@RequiredArgsConstructor
public class NotificationTypeController {

    private final NotificationTypeService notificationTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationTypeResponse>>> getActiveTypes() {
        return ApiResponse.ok("Lấy danh sách loại thông báo thành công", notificationTypeService.getActiveTypes());
    }
}