package com.dat_viet_group.datvietgroup.modules.Jobs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "application_status_logs")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // ID của user (admin) đã đổi trạng thái, chỉ lưu số, không ánh xạ sang bảng users
    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "from_status", length = 30)
    private String fromStatus;

    @Column(name = "to_status", length = 30)
    private String toStatus;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}