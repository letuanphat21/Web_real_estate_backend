package com.dat_viet_group.datvietgroup.modules.social.service.serviceimpl;

import com.dat_viet_group.datvietgroup.modules.social.service.PostReactionService;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.social.dao.PostReactionRepository;
import com.dat_viet_group.datvietgroup.modules.social.dao.PostRepository;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostReactionResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Post;
import com.dat_viet_group.datvietgroup.modules.social.entity.PostReaction;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    @Transactional
    public PostReactionResponse toggleLike(String emailOrPhone, long postId) {
        Post post = findActivePost(postId);
        long userId = userService.findByEmailOrPhone(emailOrPhone).getId();

        Optional<PostReaction> existing = postReactionRepository.findByPostAndUser(postId, userId);
        boolean liked;
        if (existing.isPresent()) {
            // Đã có bản ghi: đã thích thì bỏ thích (xóa), còn lại thì chuyển thành thích
            if (existing.get().isReaction()) {
                postReactionRepository.delete(existing.get());
                liked = false;
            } else {
                existing.get().setReaction(true);
                liked = true;
            }
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUserId(userId);
            reaction.setReaction(true);
            postReactionRepository.save(reaction);
            liked = true;
        }
        postReactionRepository.flush();

        return toResponse(postId, liked);
    }

    @Override
    @Transactional(readOnly = true)
    public PostReactionResponse getReaction(String emailOrPhone, long postId) {
        findActivePost(postId);
        long userId = userService.findByEmailOrPhone(emailOrPhone).getId();
        boolean liked = postReactionRepository.findByPostAndUser(postId, userId)
                .map(PostReaction::isReaction)
                .orElse(false);
        return toResponse(postId, liked);
    }

    private Post findActivePost(long postId) {
        return postRepository.findById(postId)
                .filter(Post::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    private PostReactionResponse toResponse(long postId, boolean liked) {
        return PostReactionResponse.builder()
                .postId(postId)
                .likeCount(postReactionRepository.countLikes(postId))
                .liked(liked)
                .build();
    }
}
