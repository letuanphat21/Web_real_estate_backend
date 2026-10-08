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
import com.dat_viet_group.datvietgroup.modules.project.dto.request.ZoneRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ZoneResponse;
import com.dat_viet_group.datvietgroup.modules.project.service.ZoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> create(@Validated @RequestBody ZoneRequest request) {
        return ApiResponse.created("Tạo phân khu thành công", zoneService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ZoneResponse>>> getAll(
            @RequestParam(required = false) Long projectId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách phân khu thành công", zoneService.getAll(projectId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy thông tin phân khu thành công", zoneService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> update(@PathVariable Long id,
            @Validated @RequestBody ZoneRequest request) {
        return ApiResponse.ok("Cập nhật phân khu thành công", zoneService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        zoneService.delete(id);
        return ApiResponse.ok("Xóa phân khu thành công");
    }
}
