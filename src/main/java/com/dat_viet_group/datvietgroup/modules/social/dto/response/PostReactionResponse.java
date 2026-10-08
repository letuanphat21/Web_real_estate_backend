package com.dat_viet_group.datvietgroup.modules.social.dto.response;

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
public class PostReactionResponse {

    private long postId;
    /** Tổng số lượt thích của bài viết. */
    private long likeCount;
    /** Người dùng hiện tại đã thích bài viết chưa. */
    private boolean liked;
}
