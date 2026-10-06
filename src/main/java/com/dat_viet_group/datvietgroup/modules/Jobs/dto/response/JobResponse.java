package com.dat_viet_group.datvietgroup.modules.Jobs.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ExperienceLevel;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String description;

    private String department;
    private String location;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private boolean salaryNegotiable;

    private Integer quantity;
    private LocalDate deadline;

    private JobStatus status;
    private String statusLabel;             // "Đang tuyển"

    private JobType type;
    private String typeLabel;               // "Toàn thời gian"

    private ExperienceLevel experienceLevel;
    private String experienceLevelLabel;    // "Junior (1-2 năm)"

    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}