package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.ZoneRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ZoneResponse;

public interface ZoneService {

    ZoneResponse create(ZoneRequest request);

    /** projectId = null thì lấy tất cả phân khu */
    Page<ZoneResponse> getAll(Long projectId, Pageable pageable);

    ZoneResponse getById(Long id);

    ZoneResponse update(Long id, ZoneRequest request);

    void delete(Long id);
}
