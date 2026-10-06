package com.dat_viet_group.datvietgroup.modules.project.dto.response;

import java.math.BigDecimal;

import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyDirection;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {

    private long id;

    private long zoneId;

    private String propertyCode;

    private BigDecimal area;

    private BigDecimal price;

    private PropertyDirection direction;

    private Integer floor;

    private PropertyStatus status;

    private Integer bedrooms;
}