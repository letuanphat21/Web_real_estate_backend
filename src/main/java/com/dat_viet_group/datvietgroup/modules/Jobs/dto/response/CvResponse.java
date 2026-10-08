package com.dat_viet_group.datvietgroup.modules.Jobs.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CvResponse {

    private Long id;
    private String title;
    private String pdfUrl;
    private LocalDateTime createdAt;
}
