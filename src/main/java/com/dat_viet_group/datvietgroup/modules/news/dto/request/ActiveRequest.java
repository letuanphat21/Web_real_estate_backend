package com.dat_viet_group.datvietgroup.modules.news.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActiveRequest {

    @NotNull(message = "Trạng thái hiển thị không được để trống")
    private Boolean active;
}
