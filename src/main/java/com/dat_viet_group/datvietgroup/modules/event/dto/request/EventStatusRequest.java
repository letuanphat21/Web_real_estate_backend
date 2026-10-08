package com.dat_viet_group.datvietgroup.modules.event.dto.request;

import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private EventStatus status;
}
