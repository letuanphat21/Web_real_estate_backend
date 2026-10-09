package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ExperienceLevel;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;

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
public class JobCreateRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    private String description;

    // ID loại công việc trong bảng job_types (Kỹ thuật, Kinh doanh...)
    private Long jobTypeId;

    @NotNull(message = "Hình thức làm việc không được để trống")
    private EmploymentType type;

    private ExperienceLevel experienceLevel;

    private String department;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String currency;            // VND, USD...

    private boolean salaryNegotiable;

    @Min(value = 1, message = "Số lượng tuyển phải >= 1")
    private Integer quantity;

    private LocalDate deadline;
}