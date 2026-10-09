package com.dat_viet_group.datvietgroup.modules.news.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.news.entity.NewImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewImageResponse {

    private long id;
    private String title;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static NewImageResponse from(NewImage image) {
        return new NewImageResponse(image.getId(), image.getTitle(), image.getImageUrl(), image.getCreatedAt());
    }
}
