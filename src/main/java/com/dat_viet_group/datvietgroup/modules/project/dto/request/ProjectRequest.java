package com.dat_viet_group.datvietgroup.modules.project.dto.request;

import java.math.BigDecimal;

import com.dat_viet_group.datvietgroup.modules.project.enums.BuildingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Tên dự án không được để trống")
    private String name;

    private String overviewImage;

    private String location;

    private String investor;

    private String consultancy;

    private String developmentModel;

    private BuildingType buildingType;

    @PositiveOrZero(message = "Quy mô dự án không được là số âm")
    private BigDecimal size;

    @PositiveOrZero(message = "Tổng mức đầu tư không được là số âm")
    private BigDecimal totalInvestment;

    private String ownershipType;
}