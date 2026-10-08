package com.dat_viet_group.datvietgroup.modules.social.service;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostReactionResponse;

public interface PostReactionService {

    /** Bật/tắt thích: chưa thích thì thích, đã thích thì bỏ thích. */
    PostReactionResponse toggleLike(String emailOrPhone, long postId);

    /** Tổng lượt thích và trạng thái thích của người dùng hiện tại. */
    PostReactionResponse getReaction(String emailOrPhone, long postId);
}
