package com.dat_viet_group.datvietgroup.modules.notification.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.notification.entity.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    /** Danh sách thông báo của tôi (tất cả loại), mới nhất trước. */
    @Query(value = "select un from UserNotification un join fetch un.notification n join fetch n.notificationType t "
            + "where un.userId = :userId and un.isDeleted = false and t.isActive = true "
            + "order by n.createdAt desc, un.id desc",
            countQuery = "select count(un) from UserNotification un join un.notification n join n.notificationType t "
                    + "where un.userId = :userId and un.isDeleted = false and t.isActive = true")
    Page<UserNotification> findMine(@Param("userId") Long userId, Pageable pageable);

    /** Như trên nhưng lọc theo loại thông báo (tab). */
    @Query(value = "select un from UserNotification un join fetch un.notification n join fetch n.notificationType t "
            + "where un.userId = :userId and un.isDeleted = false and t.id = :typeId and t.isActive = true "
            + "order by n.createdAt desc, un.id desc",
            countQuery = "select count(un) from UserNotification un join un.notification n join n.notificationType t "
                    + "where un.userId = :userId and un.isDeleted = false and t.id = :typeId and t.isActive = true")
    Page<UserNotification> findMineByType(@Param("userId") Long userId, @Param("typeId") Long typeId,
            Pageable pageable);

    /** Tìm một thông báo của đúng người dùng này, loại đang hiện (chặn thao tác trên thông báo của người khác hoặc đang bị ẩn). */
    @Query("select un from UserNotification un join un.notification n join n.notificationType t "
            + "where un.id = :id and un.userId = :userId and un.isDeleted = false and t.isActive = true")
    Optional<UserNotification> findMineById(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select count(un) from UserNotification un join un.notification n join n.notificationType t "
            + "where un.userId = :userId and un.isRead = false and un.isDeleted = false and t.isActive = true")
    long countUnread(@Param("userId") Long userId);

    /** Mỗi dòng là [typeId, số chưa đọc của loại đó]. */
    @Query("select t.id, count(un) from UserNotification un join un.notification n join n.notificationType t "
            + "where un.userId = :userId and un.isRead = false and un.isDeleted = false and t.isActive = true "
            + "group by t.id")
    List<Object[]> countUnreadGroupByType(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update UserNotification un set un.isRead = true, un.readAt = :now "
            + "where un.userId = :userId and un.isRead = false and un.isDeleted = false "
            + "and un.notification.id in (select n.id from Notification n where n.notificationType.isActive = true)")
    int markAllRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /** Khi admin xóa mềm thông báo gốc thì đánh dấu đã xóa toàn bộ bản ghi nhận của nó. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update UserNotification un set un.isDeleted = true where un.notification.id = :notificationId")
    void markDeletedByNotificationId(@Param("notificationId") Long notificationId);
}