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
public class NewsRequest {

    @NotNull(message = "Người dùng không được để trống")
    @Positive(message = "Mã người dùng phải lớn hơn 0")
    private Long userId;

    @NotNull(message = "Dự án không được để trống")
    @Positive(message = "Mã dự án phải lớn hơn 0")
    private Long projectId;

    @NotNull(message = "Danh mục tin tức không được để trống")
    @Positive(message = "Mã danh mục phải lớn hơn 0")
    private Long categoryNewId;

    @NotBlank(message = "Tiêu đề tin tức không được để trống")
    private String title;

    @NotBlank(message = "Nội dung tin tức không được để trống")
    private String content;

    private boolean active;
}