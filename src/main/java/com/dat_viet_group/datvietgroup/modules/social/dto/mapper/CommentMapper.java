package com.dat_viet_group.datvietgroup.modules.social.dto.mapper;

import org.springframework.stereotype.Component;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.CommentResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Comment;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final AuthorMapper authorMapper;

    /**
     * @param comment    bình luận
     * @param author     người viết; null nếu không tìm thấy (khi đó author chỉ có id)
     * @param replyCount số trả lời của bình luận (reply thì luôn 0)
     */
    public CommentResponse toResponse(Comment comment, User author, long replyCount) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .parentId(comment.getParentComment() == null ? null : comment.getParentComment().getId())
                .author(authorMapper.toResponse(comment.getUserId(), author))
                .content(comment.getContent())
                .replyCount(replyCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
