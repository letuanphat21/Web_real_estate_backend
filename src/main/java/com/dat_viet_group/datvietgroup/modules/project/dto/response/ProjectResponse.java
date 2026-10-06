package com.dat_viet_group.datvietgroup.modules.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.project.enums.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private long id;

    private String name;

    private String overviewImage;

    private String location;

    private String investor;

    private String consultancy;

    private String developmentModel;

    private BuildingType buildingType;

    private BigDecimal size;

    private BigDecimal totalInvestment;

    private String ownershipType;

    private LocalDateTime createdAt;
}