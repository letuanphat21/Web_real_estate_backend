package com.dat_viet_group.datvietgroup.modules.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDocumentRequest {

    @NotNull(message = "Dự án không được để trống")
    @Positive(message = "Mã dự án phải lớn hơn 0")
    private Long projectId;

    @NotBlank(message = "Tiêu đề tài liệu không được để trống")
    private String title;

    @NotBlank(message = "Đường dẫn tài liệu không được để trống")
    private String url;
}