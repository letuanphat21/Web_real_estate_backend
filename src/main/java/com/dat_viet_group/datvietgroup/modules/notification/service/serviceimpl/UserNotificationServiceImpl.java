package com.dat_viet_group.datvietgroup.modules.notification.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.notification.dao.UserNotificationRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UnreadCountResponse;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.UserNotificationResponse;
import com.dat_viet_group.datvietgroup.modules.notification.entity.Notification;
import com.dat_viet_group.datvietgroup.modules.notification.entity.UserNotification;
import com.dat_viet_group.datvietgroup.modules.notification.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserNotificationResponse> getMine(Long userId, Long typeId, int page, int size) {
        // Thứ tự sắp xếp đã nằm trong câu @Query nên Pageable không cần Sort
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<UserNotification> result = (typeId == null)
                ? userNotificationRepository.findMine(userId, pageable)
                : userNotificationRepository.findMineByType(userId, typeId, pageable);
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        long total = userNotificationRepository.countUnread(userId);

        Map<Long, Long> byType = new HashMap<>();
        for (Object[] row : userNotificationRepository.countUnreadGroupByType(userId)) {
            byType.put((Long) row[0], (Long) row[1]);
        }
        return new UnreadCountResponse(total, byType);
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long userNotificationId) {
        // Tìm theo cả userId để người này không đánh dấu được thông báo của người khác
        UserNotification un = userNotificationRepository.findMineById(userNotificationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!un.isRead()) {
            un.setRead(true);
            un.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(un);
        }
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        userNotificationRepository.markAllRead(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void delete(Long userId, Long userNotificationId) {
        // Tìm theo cả userId nên chỉ xóa được thông báo của chính mình, không thấy thì 404
        UserNotification un = userNotificationRepository.findMineById(userNotificationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        un.setDeleted(true); // xóa mềm, Notification gốc và bản của người khác không đổi
        userNotificationRepository.save(un);
    }

    private UserNotificationResponse toResponse(UserNotification un) {
        Notification n = un.getNotification();
        UserNotificationResponse response = new UserNotificationResponse();
        response.setId(un.getId());
        response.setRead(un.isRead());
        response.setReadAt(un.getReadAt());
        response.setNotificationId(n.getId());
        response.setTitle(n.getTitle());
        response.setContent(n.getContent());
        response.setImage(n.getImage());
        response.setTargetType(n.getTargetType());
        response.setTargetId(n.getTargetId());
        response.setActionUrl(n.getActionUrl());
        response.setCreatedAt(n.getCreatedAt());
        response.setNotificationTypeId(n.getNotificationType().getId());
        response.setNotificationTypeName(n.getNotificationType().getName());
        return response;
    }
}