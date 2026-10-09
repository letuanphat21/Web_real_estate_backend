package com.dat_viet_group.datvietgroup.modules.news.service;

import java.util.List;

import com.dat_viet_group.datvietgroup.modules.news.dto.request.CategoryNewRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.CategoryNewResponse;

public interface CategoryNewService {

    // onlyActive = true: chỉ danh mục đang hiển thị (trang công khai) 
    List<CategoryNewResponse> getAll(boolean onlyActive);

    CategoryNewResponse getById(long id);

    CategoryNewResponse create(CategoryNewRequest request);

    CategoryNewResponse update(long id, CategoryNewRequest request);

    void delete(long id);
}
