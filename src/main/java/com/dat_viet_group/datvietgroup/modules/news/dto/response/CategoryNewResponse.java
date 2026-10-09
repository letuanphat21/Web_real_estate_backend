package com.dat_viet_group.datvietgroup.modules.news.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.news.entity.CategoryNew;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryNewResponse {

    private long id;
    private String name;
    private String slug;
    private Boolean active;
    private LocalDateTime createdAt;

    public static CategoryNewResponse from(CategoryNew category) {
        return new CategoryNewResponse(category.getId(), category.getName(), category.getSlug(),
                category.getActive(), category.getCreatedAt());
    }
}
