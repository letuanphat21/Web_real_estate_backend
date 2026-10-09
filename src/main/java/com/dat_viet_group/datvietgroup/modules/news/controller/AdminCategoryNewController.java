package com.dat_viet_group.datvietgroup.modules.news.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.CategoryNewRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.CategoryNewResponse;
import com.dat_viet_group.datvietgroup.modules.news.service.CategoryNewService;

import lombok.RequiredArgsConstructor;

/** Admin quản lý danh mục tin tức. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. */
@RestController
@RequestMapping("/api/admin/news-categories")
@RequiredArgsConstructor
public class AdminCategoryNewController {

    private final CategoryNewService categoryNewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryNewResponse>>> getAll() {
        return ApiResponse.ok("Lấy danh mục tin tức thành công", categoryNewService.getAll(false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryNewResponse>> getById(@PathVariable long id) {
        return ApiResponse.ok("Lấy chi tiết danh mục thành công", categoryNewService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryNewResponse>> create(@Validated @RequestBody CategoryNewRequest request) {
        return ApiResponse.created("Tạo danh mục thành công", categoryNewService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryNewResponse>> update(@PathVariable long id,
            @Validated @RequestBody CategoryNewRequest request) {
        return ApiResponse.ok("Cập nhật danh mục thành công", categoryNewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        categoryNewService.delete(id);
        return ApiResponse.ok("Xóa danh mục thành công");
    }
}
