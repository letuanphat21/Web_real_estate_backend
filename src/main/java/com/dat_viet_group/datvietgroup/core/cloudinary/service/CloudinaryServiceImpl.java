package com.dat_viet_group.datvietgroup.core.cloudinary.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB / ảnh

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        validate(file);
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", "image"));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Upload ảnh thất bại: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, String folder) {
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        // Kiểm tra hết trước khi upload để tránh upload dở rồi mới phát hiện file sai
        files.forEach(this::validate);

        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                urls.add(uploadImage(file, folder));
            }
            return urls;
        } catch (AppException e) {
            deleteByUrls(urls); // rollback các ảnh đã lên
            throw e;
        }
    }

    @Override
    public void deleteByUrl(String url) {
        String publicId = extractPublicId(url);
        if (publicId == null) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException e) {
            log.warn("Xóa ảnh {} thất bại: {}", publicId, e.getMessage());
        }
    }

    @Override
    public void deleteByUrls(List<String> urls) {
        if (urls != null) {
            urls.forEach(this::deleteByUrl);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    /** https://res.cloudinary.com/<cloud>/image/upload/v123/folder/abc.jpg -> folder/abc */
    private String extractPublicId(String url) {
        if (url == null || !url.contains("/upload/")) {
            return null;
        }
        String path = url.substring(url.indexOf("/upload/") + "/upload/".length());
        if (path.matches("^v\\d+/.*")) {
            path = path.substring(path.indexOf('/') + 1);
        }
        int dot = path.lastIndexOf('.');
        return dot > 0 ? path.substring(0, dot) : path;
    }
}
