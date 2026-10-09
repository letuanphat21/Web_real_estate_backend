package com.dat_viet_group.datvietgroup.modules.news.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.ActiveRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.NewsRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewImageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewsResponse;
import com.dat_viet_group.datvietgroup.modules.news.service.NewsService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

// Admin quản lý tin tức. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. 
@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;
    private final UserService userService;

    // GET /api/admin/news?categoryId=&projectId=&keyword=&active=&page=0&size=10 (active bỏ trống = cả ẩn lẫn hiện)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách tin tức thành công",
                newsService.getAll(categoryId, projectId, keyword, active, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getById(@PathVariable long id) {
        return ApiResponse.ok("Lấy chi tiết tin tức thành công", newsService.getById(id, false));
    }

    // form-data: projectId, categoryId, title, content, active (không bắt buộc), images (File, nhiều, không bắt buộc)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<NewsResponse>> create(
            @Validated @ModelAttribute NewsRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        long authorId = userService.findByEmailOrPhone(authentication.getName()).getId();
        return ApiResponse.created("Tạo tin tức thành công", newsService.create(request, images, authorId));
    }

    // JSON: projectId, categoryId, title, content, active
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> update(@PathVariable long id,
            @Validated @RequestBody NewsRequest request) {
        return ApiResponse.ok("Cập nhật tin tức thành công", newsService.update(id, request));
    }

    // JSON: { "active": false } — ẩn / hiện tin tức
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<NewsResponse>> updateActive(@PathVariable long id,
            @Validated @RequestBody ActiveRequest request) {
        return ApiResponse.ok("Cập nhật trạng thái hiển thị thành công",
                newsService.updateActive(id, request.getActive()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        newsService.delete(id);
        return ApiResponse.ok("Xóa tin tức thành công");
    }

    // form-data: images (File, chọn nhiều)
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<NewImageResponse>>> addImages(@PathVariable long id,
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.created("Thêm ảnh thành công", newsService.addImages(id, images));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable long id, @PathVariable long imageId) {
        newsService.deleteImage(id, imageId);
        return ApiResponse.ok("Xóa ảnh thành công");
    }
}
