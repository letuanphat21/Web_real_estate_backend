package com.dat_viet_group.datvietgroup.modules.notification.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.notification.dao.NotificationRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dao.NotificationTypeRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dao.UserNotificationRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationResponse;
import com.dat_viet_group.datvietgroup.modules.notification.entity.Notification;
import com.dat_viet_group.datvietgroup.modules.notification.entity.NotificationType;
import com.dat_viet_group.datvietgroup.modules.notification.entity.UserNotification;
import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserNotificationRepository userNotificationRepository;

    // ================= ADMIN =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getAll(Long typeId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> result = (typeId == null)
                ? notificationRepository.findAll(pageable)
                : notificationRepository.findByNotificationTypeId(typeId, pageable);
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public NotificationResponse create(CreateNotificationRequest request, Long adminId) {
        NotificationType type = notificationTypeRepository.findById(request.getNotificationTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND));

        // Không chỉ định người nhận thì gửi cho tất cả tài khoản đang hoạt động
        List<Long> receivers = (request.getUserIds() == null || request.getUserIds().isEmpty())
                ? notificationRepository.findAllActiveUserIds()
                : notificationRepository.findExistingUserIds(request.getUserIds().stream().distinct().toList());
        // Kiểm tra trước khi lưu để không tạo thông báo không có ai nhận
        if (receivers.isEmpty()) {
            throw new AppException(ErrorCode.NOTIFICATION_NO_RECEIVER);
        }

        Notification notification = new Notification();
        notification.setNotificationType(type);
        notification.setCreatedBy(adminId);
        notification.setTitle(request.getTitle().trim());
        notification.setContent(request.getContent());
        notification.setImage(request.getImage());
        notification.setTargetType(request.getTargetType());
        notification.setTargetId(request.getTargetId());
        notification.setActionUrl(request.getActionUrl());
        notification = notificationRepository.save(notification);

        saveReceivers(notification, receivers);

        return toResponse(notification);
    }

    @Override
    @Transactional
    public NotificationResponse update(Long id, UpdateNotificationRequest request) {
        Notification notification = findOrThrow(id);

        if (request.getNotificationTypeId() != null) {
            NotificationType type = notificationTypeRepository.findById(request.getNotificationTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND));
            notification.setNotificationType(type);
        }
        // Chỉ ghi đè những trường được gửi lên (khác null)
        if (request.getTitle() != null) {
            notification.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            notification.setContent(request.getContent());
        }
        if (request.getImage() != null) {
            notification.setImage(request.getImage());
        }
        if (request.getTargetType() != null) {
            notification.setTargetType(request.getTargetType());
        }
        if (request.getTargetId() != null) {
            notification.setTargetId(request.getTargetId());
        }
        if (request.getActionUrl() != null) {
            notification.setActionUrl(request.getActionUrl());
        }
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Notification notification = findOrThrow(id);
        // Xóa mềm thông báo gốc, đồng thời ẩn nó khỏi danh sách của mọi người nhận
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
        userNotificationRepository.markDeletedByNotificationId(id);
    }

    // ================= NỘI BỘ =================

    @Override
    @Transactional
    public void send(Long userId, String typeName, String title, String content,
            TargetType targetType, Long targetId, String actionUrl) {
        // Chưa có loại này thì tự tạo (đang hiện)
        NotificationType type = notificationTypeRepository.findByName(typeName).orElseGet(() -> {
            NotificationType created = new NotificationType();
            created.setName(typeName);
            created.setActive(true);
            return notificationTypeRepository.save(created);
        });

        Notification notification = new Notification();
        notification.setNotificationType(type);
        notification.setCreatedBy(null); // thông báo do hệ thống gửi, không có admin
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setActionUrl(actionUrl);
        notification = notificationRepository.save(notification);

        saveReceivers(notification, List.of(userId));
    }

    // ================= HÀM PHỤ =================

    private void saveReceivers(Notification notification, List<Long> userIds) {
        List<UserNotification> rows = userIds.stream().map(userId -> {
            UserNotification un = new UserNotification();
            un.setUserId(userId);
            un.setNotification(notification);
            return un; // isRead, isDeleted mặc định false
        }).toList();
        userNotificationRepository.saveAll(rows);
    }

    private Notification findOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse response = new NotificationResponse();
        response.setId(n.getId());
        response.setNotificationTypeId(n.getNotificationType().getId());
        response.setNotificationTypeName(n.getNotificationType().getName());
        response.setCreatedBy(n.getCreatedBy());
        response.setTitle(n.getTitle());
        response.setContent(n.getContent());
        response.setImage(n.getImage());
        response.setTargetType(n.getTargetType());
        response.setTargetId(n.getTargetId());
        response.setActionUrl(n.getActionUrl());
        response.setCreatedAt(n.getCreatedAt());
        return response;
    }
}