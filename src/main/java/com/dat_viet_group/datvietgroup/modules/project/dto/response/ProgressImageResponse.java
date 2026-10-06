package com.dat_viet_group.datvietgroup.modules.project.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressImageResponse {

    private long id;

    private long progressId;

    private String imageUrl;

    private LocalDateTime createdAt;
}