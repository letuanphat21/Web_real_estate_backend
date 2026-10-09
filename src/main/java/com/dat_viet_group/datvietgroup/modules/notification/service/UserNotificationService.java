
package com.dat_viet_group.datvietgroup.modules.notification.service;
//- Các thao tác của người dùng trên thông báo của chính họ. Mọi hàm đều nhận `userId`.
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UnreadCountResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UserNotificationResponse;

public interface UserNotificationService {

    /** Thông báo của tôi. typeId = null thì lấy tất cả loại. */
    PageResponse<UserNotificationResponse> getMine(Long userId, Long typeId, int page, int size);

    UnreadCountResponse getUnreadCount(Long userId);

    void markRead(Long userId, Long userNotificationId);

    void markAllRead(Long userId);

    /** Gỡ một thông báo khỏi danh sách của tôi (is_deleted = true). Không thấy thì 404. */
    void delete(Long userId, Long userNotificationId);
}