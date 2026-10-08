package com.dat_viet_group.datvietgroup.modules.event.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String title;

    @Size(max = 500, message = "Nội dung tối đa 500 ký tự")
    private String content;

    @Size(max = 200, message = "Địa điểm tối đa 200 ký tự")
    private String location;

    @Min(value = 0, message = "Số người tham gia tối đa phải >= 0 (0 = không giới hạn)")
    private int maxAttendees;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
}
