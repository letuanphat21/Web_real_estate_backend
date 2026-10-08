package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.ZoneRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ZoneResponse;

public interface ZoneService {

    /** image = ảnh phân khu (có thể null) */
    ZoneResponse create(ZoneRequest request, MultipartFile image);

    /** projectId = null thì lấy tất cả phân khu */
    Page<ZoneResponse> getAll(Long projectId, Pageable pageable);

    ZoneResponse getById(Long id);

    /** image = null thì giữ ảnh cũ, có ảnh mới thì thay và xóa ảnh cũ trên Cloudinary */
    ZoneResponse update(Long id, ZoneRequest request, MultipartFile image);

    void delete(Long id);
}
