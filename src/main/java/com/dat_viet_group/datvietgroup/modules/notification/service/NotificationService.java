package com.dat_viet_group.datvietgroup.modules.notification.service;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationResponse;
import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;

public interface NotificationService {

    // Tên loại thông báo dùng cho thông báo tự động của hệ thống.
    // send() tự tạo loại nếu chưa có, nên cần thống nhất tên trong team.
    String TYPE_JOB = "Tuyển dụng";

    // ===== Admin =====

    PageResponse<NotificationResponse> getAll(Long typeId, int page, int size);

    NotificationResponse getById(Long id);

    /** Tạo thông báo và gửi ngay. userIds rỗng thì gửi cho tất cả tài khoản đang hoạt động. */
    NotificationResponse create(CreateNotificationRequest request, Long adminId);

    NotificationResponse update(Long id, UpdateNotificationRequest request);

    /** Xóa mềm thông báo; các bản ghi nhận của người dùng cũng được đánh dấu đã xóa. */
    void delete(Long id);

    /** Đảo trạng thái ẩn/hiện của thông báo (ẩn thì người dùng không còn thấy). */
    NotificationResponse toggle(Long id);

    // ===== Dùng nội bộ: module khác (Jobs, Events, Booking...) gọi hàm này =====

    /**
     * Gửi một thông báo cho một người dùng.
     *
     * @param typeName   tên loại thông báo (tự tạo nếu chưa có), ví dụ TYPE_JOB
     * @param targetType đối tượng liên quan, ví dụ TargetType.APPLICATION
     * @param targetId   id của đối tượng liên quan, ví dụ id đơn ứng tuyển
     * @param actionUrl  đường dẫn FE mở ra khi bấm "Xem" (có thể null)
     */
    void send(Long userId, String typeName, String title, String content,
            TargetType targetType, Long targetId, String actionUrl);
}