package com.dat_viet_group.datvietgroup.modules.notification.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;

    // ID của user (admin) tạo thông báo, chỉ lưu số, không import entity User
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image", length = 500)
    private String image;

    @Enumerated(EnumType.STRING) // lưu vào DB tên chữ của giá trị
    @Column(name = "target_type",length=50)
    private TargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "notification")
    private List<UserNotification> userNotifications;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}