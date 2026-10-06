package com.dat_viet_group.datvietgroup.modules.project.dto.request;

import com.dat_viet_group.datvietgroup.modules.project.enums.ZoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneRequest {

    @NotNull(message = "Dự án không được để trống")
    @Positive(message = "Mã dự án phải lớn hơn 0")
    private Long projectId;

    @NotBlank(message = "Tên phân khu không được để trống")
    private String name;

    private String description;

    private ZoneStatus status;

    private String imageUrl;
}