package com.dat_viet_group.datvietgroup.core.cloudinary.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    /** Upload 1 ảnh, trả về URL (https). */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Upload nhiều ảnh, trả về danh sách URL theo đúng thứ tự file gửi lên.
     * Nếu 1 ảnh lỗi thì các ảnh đã upload trước đó sẽ bị xóa (rollback) rồi ném AppException.
     */
    List<String> uploadImages(List<MultipartFile> files, String folder);

    /** Xóa ảnh theo URL (bỏ qua nếu URL không thuộc Cloudinary). */
    void deleteByUrl(String url);

    /** Xóa nhiều ảnh theo URL. */
    void deleteByUrls(List<String> urls);
}
