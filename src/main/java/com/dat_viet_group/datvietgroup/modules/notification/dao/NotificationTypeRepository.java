package com.dat_viet_group.datvietgroup.modules.notification.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.notification.entity.NotificationType;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findByName(String name);

    /** Chỉ lấy loại đang hiện, dùng cho các tab bên phía người dùng. */
    @Query("select t from NotificationType t where t.isActive = true order by t.id")
    List<NotificationType> findAllActive();
}