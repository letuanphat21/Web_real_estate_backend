package com.dat_viet_group.datvietgroup.modules.social.dto.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Post;
import com.dat_viet_group.datvietgroup.modules.social.entity.PostImage;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final AuthorMapper authorMapper;

    /**
     * @param post   bài viết
     * @param author người đăng; null nếu không tìm thấy (khi đó author chỉ có id)
     */
    public PostResponse toResponse(Post post, User author) {
        List<String> imageUrls = post.getPostImages() == null ? List.of()
                : post.getPostImages().stream().map(PostImage::getImageUrl).toList();
        return PostResponse.builder()
                .id(post.getId())
                .author(authorMapper.toResponse(post.getUserId(), author))
                .content(post.getContent())
                .imageUrls(imageUrls)
                .videoUrl(post.getVideoUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
