package com.dat_viet_group.datvietgroup.modules.social.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostResponse;

public interface PostService {

    /** Đăng bài: content (có thể rỗng nếu có ảnh) + danh sách ảnh (có thể rỗng nếu có content). */
    PostResponse createPost(String emailOrPhone, String content, List<MultipartFile> images);

    PostResponse getPost(long id);

    Page<PostResponse> getPosts(Pageable pageable);
}
