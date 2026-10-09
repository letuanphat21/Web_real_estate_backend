package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ExperienceLevel;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobUpdateRequest {

    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    private String description;

    private Long jobTypeId;

    private EmploymentType type;

    private ExperienceLevel experienceLevel;

    private String department;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String currency;

    private Boolean salaryNegotiable;   // dùng Boolean (wrapper) để phân biệt null vs false

    @Min(value = 1, message = "Số lượng tuyển phải >= 1")
    private Integer quantity;

    private LocalDate deadline;

    private JobStatus status;           // update thì được đổi trạng thái
}