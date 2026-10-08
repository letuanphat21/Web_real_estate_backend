package com.dat_viet_group.datvietgroup.modules.social.dto.response;

import java.time.LocalDateTime;

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
public class CommentResponse {

    private long id;
    private long postId;
    private Long parentId;
    private Long userId;
    private String content;
    private long replyCount;
    private LocalDateTime createdAt;
}
