package com.dat_viet_group.datvietgroup.modules.social.service.serviceimpl;

import com.dat_viet_group.datvietgroup.modules.social.service.CommentService;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.social.dao.CommentRepository;
import com.dat_viet_group.datvietgroup.modules.social.dao.PostRepository;
import com.dat_viet_group.datvietgroup.modules.social.dto.request.CommentCreateRequest;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.CommentResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Comment;
import com.dat_viet_group.datvietgroup.modules.social.entity.Post;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final int MAX_CONTENT_LENGTH = 500;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CommentResponse createComment(String emailOrPhone, CommentCreateRequest request) {
        // Check lại ở backend dù đã có @Valid
        if (!StringUtils.hasText(request.getContent())) {
            throw new AppException(ErrorCode.COMMENT_EMPTY);
        }
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new AppException(ErrorCode.INVALID_INPUT_FORMAT,
                    "Nội dung bình luận tối đa " + MAX_CONTENT_LENGTH + " ký tự");
        }

        Post post = postRepository.findById(request.getPostId())
                .filter(Post::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .filter(Comment::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            if (parent.getPost().getId() != post.getId()) {
                throw new AppException(ErrorCode.COMMENT_PARENT_INVALID);
            }
            // Chỉ cho 2 cấp: trả lời một reply thì gắn vào bình luận gốc
            if (parent.getParentComment() != null) {
                parent = parent.getParentComment();
            }
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent().trim());
        comment.setUserId(userService.findByEmailOrPhone(emailOrPhone).getId());
        comment.setPost(post);
        comment.setParentComment(parent);
        comment.setActive(true);
        comment.setCreatedAt(LocalDateTime.now());

        return toResponse(commentRepository.save(comment), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPost(long postId, Pageable pageable) {
        postRepository.findById(postId)
                .filter(Post::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return commentRepository.findRootComments(postId, pageable)
                .map(c -> toResponse(c, commentRepository.countReplies(c.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getReplies(long commentId, Pageable pageable) {
        commentRepository.findById(commentId)
                .filter(Comment::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        return commentRepository.findReplies(commentId, pageable).map(c -> toResponse(c, 0));
    }

    private CommentResponse toResponse(Comment comment, long replyCount) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .parentId(comment.getParentComment() == null ? null : comment.getParentComment().getId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .replyCount(replyCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
