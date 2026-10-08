package com.dat_viet_group.datvietgroup.core.cloudinary.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.core.common.ApiResponse;

import lombok.RequiredArgsConstructor;

/** CHỈ DÙNG ĐỂ TEST — xóa controller này (và endpoint trong Endpoints.java) trước khi lên production. */
@RestController
@RequestMapping("/api/test/cloudinary")
@RequiredArgsConstructor
public class CloudinaryTestController {

    private final CloudinaryService cloudinaryService;

    /** form-data: images (File, chọn nhiều), folder (Text, mặc định "test") */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> upload(
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam(defaultValue = "test") String folder) {
        List<String> urls = cloudinaryService.uploadImages(images, folder);
        return ApiResponse.ok("Upload thành công " + urls.size() + " ảnh", urls);
    }

    /** body JSON: ["https://res.cloudinary.com/...jpg", ...] */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestBody List<String> urls) {
        cloudinaryService.deleteByUrls(urls);
        return ApiResponse.ok("Đã xóa " + urls.size() + " ảnh");
    }
}
