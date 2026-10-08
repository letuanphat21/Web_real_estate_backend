package com.dat_viet_group.datvietgroup.modules.notification.service;

import java.util.List;

import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationTypeResponse;

public interface NotificationTypeService {

    /** Người dùng: chỉ các loại đang hiện (làm tab lọc). */
    List<NotificationTypeResponse> getActiveTypes();

    /** Admin: tất cả loại, kể cả loại đang ẩn. */
    List<NotificationTypeResponse> getAll();

    NotificationTypeResponse getById(Long id);

    NotificationTypeResponse create(CreateNotificationTypeRequest request);

    NotificationTypeResponse update(Long id, UpdateNotificationTypeRequest request);

    /** Xóa mềm (gán deleted_at). Chặn nếu còn thông báo đang dùng loại này. */
    void delete(Long id);

    /** Đảo is_active (ẩn/hiện). */
    NotificationTypeResponse toggle(Long id);
}