package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private JobStatus status;
}
