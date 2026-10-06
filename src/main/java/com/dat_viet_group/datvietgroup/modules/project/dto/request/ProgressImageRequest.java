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
public class ProgressImageRequest {

    @NotNull(message = "Tiến độ dự án không được để trống")
    @Positive(message = "Mã tiến độ phải lớn hơn 0")
    private Long progressId;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    private String imageUrl;
}