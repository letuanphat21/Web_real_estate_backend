package com.dat_viet_group.datvietgroup.modules.Jobs.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "applications")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // Tham chiếu sang module User bằng ID
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private Cv cv;

    // Người duyệt đơn (admin), lúc mới nộp chưa có
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "cv_url", length = 500)
    private String cvUrl;

    @Column(name = "cv_file_name", length = 255)
    private String cvFileName;

    @Column(name = "cv_file_size")
    private Long cvFileSize;

    @Column(name = "cv_mime_type", length = 100)
    private String cvMimeType;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "sources", length = 100)
    private String sources;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ApplicationStatus status;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "application")
    private List<ApplicationStatusLog> statusLogs;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}