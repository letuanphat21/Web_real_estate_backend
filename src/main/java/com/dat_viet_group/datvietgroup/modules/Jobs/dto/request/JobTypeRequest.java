package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dùng chung cho tạo và sửa loại công việc. */
@Getter
@Setter
@NoArgsConstructor
public class JobTypeRequest {

    @NotBlank(message = "Tên loại công việc không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    // Không gửi thì: tạo mới = hiện, sửa = giữ nguyên
    private Boolean isActive;
}
