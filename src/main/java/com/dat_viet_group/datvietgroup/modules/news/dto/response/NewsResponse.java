package com.dat_viet_group.datvietgroup.modules.news.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsResponse {

    private long id;
    private String title;
    private String content;
    private boolean active;
    private CategorySummary category;
    private ProjectSummary project;
    private AuthorSummary author;
    private List<NewImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record CategorySummary(long id, String name, String slug) {
    }

    public record ProjectSummary(long id, String name) {
    }

    public record AuthorSummary(long id, String fullName, String avatarUrl) {
    }
}
