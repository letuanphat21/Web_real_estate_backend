package com.dat_viet_group.datvietgroup.modules.notification.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.notification.dao.NotificationRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dao.NotificationTypeRepository;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.CreateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.request.UpdateNotificationTypeRequest;
import com.dat_viet_group.datvietgroup.modules.notification.dto.response.NotificationTypeResponse;
import com.dat_viet_group.datvietgroup.modules.notification.entity.NotificationType;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTypeResponse> getActiveTypes() {
        return notificationTypeRepository.findAllActive().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTypeResponse> getAll() {
        return notificationTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTypeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public NotificationTypeResponse create(CreateNotificationTypeRequest request) {
        NotificationType type = new NotificationType();
        type.setName(request.getName().trim());
        // isActive không gửi lên thì mặc định hiện
        type.setActive(request.getIsActive() == null || request.getIsActive());
        return toResponse(notificationTypeRepository.save(type));
    }

    @Override
    @Transactional
    public NotificationTypeResponse update(Long id, UpdateNotificationTypeRequest request) {
        NotificationType type = findOrThrow(id);
        type.setName(request.getName().trim());
        if (request.getIsActive() != null) {
            type.setActive(request.getIsActive());
        }
        return toResponse(notificationTypeRepository.save(type));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        NotificationType type = findOrThrow(id);
        // Còn thông báo (chưa xóa) dùng loại này thì không xóa, chỉ cho ẩn
        if (notificationRepository.existsByNotificationTypeId(id)) {
            throw new AppException(ErrorCode.NOTIFICATION_TYPE_IN_USE);
        }
        // Xóa mềm: @SQLRestriction trên entity tự ẩn loại này khỏi mọi truy vấn sau đó
        type.setDeletedAt(LocalDateTime.now());
        notificationTypeRepository.save(type);
    }

    @Override
    @Transactional
    public NotificationTypeResponse toggle(Long id) {
        NotificationType type = findOrThrow(id);
        type.setActive(!type.isActive());
        return toResponse(notificationTypeRepository.save(type));
    }

    private NotificationType findOrThrow(Long id) {
        return notificationTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND));
    }

    private NotificationTypeResponse toResponse(NotificationType type) {
        NotificationTypeResponse response = new NotificationTypeResponse();
        response.setId(type.getId());
        response.setName(type.getName());
        response.setIsActive(type.isActive());
        response.setCreatedAt(type.getCreatedAt());
        return response;
    }
}