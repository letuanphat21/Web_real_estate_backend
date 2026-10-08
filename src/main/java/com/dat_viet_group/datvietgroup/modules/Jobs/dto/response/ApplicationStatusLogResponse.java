package com.dat_viet_group.datvietgroup.modules.Jobs.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationStatusLogResponse {

    private Long id;
    private Long changedBy;
    private String fromStatus;      // null ở lần nộp đơn đầu tiên
    private String toStatus;
    private String note;
    private LocalDateTime createdAt;
}
