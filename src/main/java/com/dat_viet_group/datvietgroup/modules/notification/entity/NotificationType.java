package com.dat_viet_group.datvietgroup.modules.notification.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "notification_types")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL") // xóa mềm: truy vấn tự bỏ qua bản ghi đã xóa
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "notificationType")
    private List<Notification> notifications;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}