package com.dat_viet_group.datvietgroup.modules.Jobs.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ExperienceLevel;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "jobs")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    // Tham chiếu sang module User bằng ID, không import entity User
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "job_type_id")
    private JobType jobType;

    // Hình thức làm việc: FULL_TIME, PART_TIME...
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 30)
    private EmploymentType employmentType;

    // Cấp độ kinh nghiệm: FRESHER, JUNIOR...
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 30)
    private ExperienceLevel experienceLevel;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "salary_min")
    private BigDecimal salaryMin;

    @Column(name = "salary_max")
    private BigDecimal salaryMax;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "salary_negotiable")
    private boolean salaryNegotiable;

    @Column(name = "experience", length = 100)
    private String experience;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Vector nhúng phục vụ chatbot RAG (cùng kiểu với Property.vectorEmbedding). Chưa dùng ở module này.
    @ToString.Exclude
    @Column(name = "embedding", columnDefinition = "text")
    private String embedding;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private JobStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "job")
    private List<Application> applications;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}