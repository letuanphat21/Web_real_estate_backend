package com.dat_viet_group.datvietgroup.modules.project.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private long id;

    private long projectId;

    private String question;

    private String answer;

    private LocalDateTime createdAt;
}