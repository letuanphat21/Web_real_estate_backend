package com.dat_viet_group.datvietgroup.modules.project.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.ProjectRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ProjectResponse;
import com.dat_viet_group.datvietgroup.modules.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /** form-data: các field của dự án + image (ảnh tổng quan, không bắt buộc) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectResponse>> create(
            @Validated @ModelAttribute ProjectRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.created("Tạo dự án thành công", projectService.create(request, image));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách dự án thành công", projectService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy thông tin dự án thành công", projectService.getById(id));
    }

    /** form-data: gửi image mới thì thay ảnh cũ, không gửi thì giữ nguyên */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectResponse>> update(@PathVariable Long id,
            @Validated @ModelAttribute ProjectRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.ok("Cập nhật dự án thành công", projectService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ApiResponse.ok("Xóa dự án thành công");
    }
}
