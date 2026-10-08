package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.ProjectRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ProjectResponse;

public interface ProjectService {

    /** image = ảnh tổng quan dự án (có thể null) */
    ProjectResponse create(ProjectRequest request, MultipartFile image);

    Page<ProjectResponse> getAll(Pageable pageable);

    ProjectResponse getById(Long id);

    /** image = null thì giữ ảnh cũ, có ảnh mới thì thay và xóa ảnh cũ trên Cloudinary */
    ProjectResponse update(Long id, ProjectRequest request, MultipartFile image);

    void delete(Long id);
}
