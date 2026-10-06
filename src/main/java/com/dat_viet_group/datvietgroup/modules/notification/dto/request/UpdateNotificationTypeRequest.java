package com.dat_viet_group.datvietgroup.modules.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationTypeRequest {

    @NotBlank(message = "Tên loại thông báo không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    private Boolean isActive;
}