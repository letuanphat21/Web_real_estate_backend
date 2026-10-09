package com.dat_viet_group.datvietgroup.modules.news.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.NewsRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewImageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewsResponse;

public interface NewsService {

    // active = null: lấy cả tin ẩn lẫn hiện (dành cho admin)
    PageResponse<NewsResponse> getAll(Long categoryId, Long projectId, String keyword, Boolean active,
            Pageable pageable);

    // onlyActive = true: tin bị ẩn coi như không tồn tại (dành cho trang công khai)
    NewsResponse getById(long id, boolean onlyActive);

    NewsResponse create(NewsRequest request, List<MultipartFile> images, long authorId);

    NewsResponse update(long id, NewsRequest request);

    NewsResponse updateActive(long id, boolean active);

    void delete(long id);

    List<NewImageResponse> addImages(long newsId, List<MultipartFile> images);

    void deleteImage(long newsId, long imageId);
}
