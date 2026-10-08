package com.dat_viet_group.datvietgroup.modules.project.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.dat_viet_group.datvietgroup.modules.project.dto.request.QuestionRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.QuestionResponse;
import com.dat_viet_group.datvietgroup.modules.project.service.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> create(@Validated @RequestBody QuestionRequest request) {
        return ApiResponse.created("Tạo câu hỏi thành công", questionService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> getAll(
            @RequestParam(required = false) Long projectId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách câu hỏi thành công", questionService.getAll(projectId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy thông tin câu hỏi thành công", questionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> update(@PathVariable Long id,
            @Validated @RequestBody QuestionRequest request) {
        return ApiResponse.ok("Cập nhật câu hỏi thành công", questionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ApiResponse.ok("Xóa câu hỏi thành công");
    }
}
