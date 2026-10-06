package com.dat_viet_group.datvietgroup.modules.project.dto.response;

import com.dat_viet_group.datvietgroup.modules.project.enums.ZoneStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneResponse {

    private long id;

    private long projectId;

    private String name;

    private String description;

    private ZoneStatus status;

    private String imageUrl;
}