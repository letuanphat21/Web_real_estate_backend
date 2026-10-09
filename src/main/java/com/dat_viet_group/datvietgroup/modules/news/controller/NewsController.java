package com.dat_viet_group.datvietgroup.modules.news.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.CategoryNewResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewsResponse;
import com.dat_viet_group.datvietgroup.modules.news.service.CategoryNewService;
import com.dat_viet_group.datvietgroup.modules.news.service.NewsService;

import lombok.RequiredArgsConstructor;

/** API công khai: chỉ trả tin tức và danh mục đang hiển thị */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final CategoryNewService categoryNewService;

    // GET /api/news?categoryId=1&projectId=1&keyword=abc&page=0&size=10&sort=createdAt,desc
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách tin tức thành công",
                newsService.getAll(categoryId, projectId, keyword, true, pageable));
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getById(@PathVariable long id) {
        return ApiResponse.ok("Lấy chi tiết tin tức thành công", newsService.getById(id, true));
    }

    @GetMapping("/news-categories")
    public ResponseEntity<ApiResponse<List<CategoryNewResponse>>> getCategories() {
        return ApiResponse.ok("Lấy danh mục tin tức thành công", categoryNewService.getAll(true));
    }
}
