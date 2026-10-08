package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationStatusUpdateRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private ApplicationStatus status;

    // Ghi chú của admin, lưu vào admin_note và vào lịch sử đổi trạng thái
    private String note;
}
