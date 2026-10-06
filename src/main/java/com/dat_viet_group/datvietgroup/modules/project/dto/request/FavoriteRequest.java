package com.dat_viet_group.datvietgroup.modules.project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {

    @NotNull(message = "Dự án không được để trống")
    @Positive(message = "Mã dự án phải lớn hơn 0")
    private Long projectId;

    @NotNull(message = "Người dùng không được để trống")
    @Positive(message = "Mã người dùng phải lớn hơn 0")
    private Long userId;
}