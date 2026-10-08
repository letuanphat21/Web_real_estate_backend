package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.PropertyRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.PropertyResponse;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;

public interface PropertyService {

    PropertyResponse create(PropertyRequest request);

    /** zoneId, status = null thì không lọc theo tiêu chí đó */
    Page<PropertyResponse> getAll(Long zoneId, PropertyStatus status, Pageable pageable);

    PropertyResponse getById(Long id);

    PropertyResponse update(Long id, PropertyRequest request);

    void delete(Long id);
}
