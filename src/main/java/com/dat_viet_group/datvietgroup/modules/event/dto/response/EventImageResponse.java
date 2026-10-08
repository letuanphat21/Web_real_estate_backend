package com.dat_viet_group.datvietgroup.modules.event.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.event.entity.EventImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventImageResponse {

    private long id;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static EventImageResponse from(EventImage image) {
        return new EventImageResponse(image.getId(), image.getImageUrl(), image.getCreatedAt());
    }
}
