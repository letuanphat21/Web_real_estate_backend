package com.dat_viet_group.datvietgroup.modules.Jobs.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationResponse {

    private Long id;

    private Long jobId;
    private String jobTitle;

    private Long userId;
    private Long cvId;

    private String fullName;
    private String email;
    private String phone;

    private String cvUrl;
    private String cvFileName;
    private String coverLetter;
    private String sources;

    private ApplicationStatus status;
    private String statusLabel;             // "Đang xem xét"

    private String adminNote;
    private Long reviewedBy;
    private LocalDateTime statusChangedAt;
    private LocalDateTime createdAt;
}
