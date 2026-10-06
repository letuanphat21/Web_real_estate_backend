package com.dat_viet_group.datvietgroup.modules.project.dto.request;

import java.math.BigDecimal;

import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyDirection;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequest {

    @NotNull(message = "Phân khu không được để trống")
    @Positive(message = "Mã phân khu phải lớn hơn 0")
    private Long zoneId;

    @NotBlank(message = "Mã bất động sản không được để trống")
    private String propertyCode;

    @Positive(message = "Diện tích phải lớn hơn 0")
    private BigDecimal area;

    @PositiveOrZero(message = "Giá không được là số âm")
    private BigDecimal price;

    private PropertyDirection direction;

    @PositiveOrZero(message = "Tầng không được là số âm")
    private Integer floor;

    private PropertyStatus status;

    @PositiveOrZero(message = "Số phòng ngủ không được là số âm")
    private Integer bedrooms;
}