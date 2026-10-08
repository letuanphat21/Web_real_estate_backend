package com.dat_viet_group.datvietgroup.modules.project.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.PropertyRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.PropertyResponse;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;
import com.dat_viet_group.datvietgroup.modules.project.service.PropertyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponse>> create(@Validated @RequestBody PropertyRequest request) {
        return ApiResponse.created("Tạo bất động sản thành công", propertyService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PropertyResponse>>> getAll(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) PropertyStatus status,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách bất động sản thành công",
                propertyService.getAll(zoneId, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy thông tin bất động sản thành công", propertyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> update(@PathVariable Long id,
            @Validated @RequestBody PropertyRequest request) {
        return ApiResponse.ok("Cập nhật bất động sản thành công", propertyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ApiResponse.ok("Xóa bất động sản thành công");
    }
}
