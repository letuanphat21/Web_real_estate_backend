package com.dat_viet_group.datvietgroup.modules.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.social.dto.request.CommentCreateRequest;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.CommentResponse;

public interface CommentService {

    CommentResponse createComment(String emailOrPhone, CommentCreateRequest request);

    /** Bình luận gốc của bài viết. */
    Page<CommentResponse> getCommentsByPost(long postId, Pageable pageable);

    /** Các trả lời của một bình luận. */
    Page<CommentResponse> getReplies(long commentId, Pageable pageable);
}
