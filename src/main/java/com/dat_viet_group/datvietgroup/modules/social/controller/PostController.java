package com.dat_viet_group.datvietgroup.modules.social.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostResponse;
import com.dat_viet_group.datvietgroup.modules.social.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * form-data: content (Text), images (File, chọn nhiều), video (File, 1 video ≤ 20MB) — đều tùy chọn
     * nhưng cần ít nhất 1 trong 3
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            Authentication authentication,
            @RequestParam(value = "content", required = false) String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video) {
        PostResponse post = postService.createPost(authentication.getName(), content, images, video);
        return ApiResponse.created("Đăng bài thành công", post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable long id) {
        return ApiResponse.ok("Lấy bài viết thành công", postService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok("Lấy danh sách bài viết thành công", postService.getPosts(pageable));
    }
}
