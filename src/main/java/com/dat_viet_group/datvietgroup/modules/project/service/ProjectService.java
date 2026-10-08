package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.ProjectRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ProjectResponse;

public interface ProjectService {

    ProjectResponse create(ProjectRequest request);

    Page<ProjectResponse> getAll(Pageable pageable);

    ProjectResponse getById(Long id);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);
}
