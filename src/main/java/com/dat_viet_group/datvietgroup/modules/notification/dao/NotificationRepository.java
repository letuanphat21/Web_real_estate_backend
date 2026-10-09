package com.dat_viet_group.datvietgroup.modules.notification.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByNotificationTypeId(Long notificationTypeId, Pageable pageable);

    boolean existsByNotificationTypeId(Long notificationTypeId);

    // Hai query dưới đây đọc bảng users qua entity "User" trong JPQL (không import entity vào module).
    /** ID của mọi tài khoản đang hoạt động, dùng khi gửi thông báo cho tất cả. */
    @Query("select u.id from User u where u.isActive = true and u.isDeleted = false")
    List<Long> findAllActiveUserIds();

    /** Lọc ra những ID thật sự tồn tại trong danh sách admin nhập. */
    @Query("select u.id from User u where u.id in :ids")
    List<Long> findExistingUserIds(@Param("ids") List<Long> ids);
}