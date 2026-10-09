package com.dat_viet_group.datvietgroup.modules.social.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private long id;
    private AuthorResponse author;
    private String content;
    private List<String> imageUrls;
    private String videoUrl;
    private LocalDateTime createdAt;
}
