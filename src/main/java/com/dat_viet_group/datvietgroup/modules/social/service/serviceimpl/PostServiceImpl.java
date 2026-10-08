package com.dat_viet_group.datvietgroup.modules.social.service.serviceimpl;

import com.dat_viet_group.datvietgroup.modules.social.service.PostService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.social.dao.PostRepository;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Post;
import com.dat_viet_group.datvietgroup.modules.social.entity.PostImage;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String IMAGE_FOLDER = "posts";
    private static final int MAX_IMAGES = 10;
    private static final int MAX_CONTENT_LENGTH = 1000;

    private final PostRepository postRepository;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public PostResponse createPost(String emailOrPhone, String content, List<MultipartFile> images) {
        // Bỏ các part rỗng (form-data gửi "images" trống vẫn tạo ra 1 part rỗng)
        List<MultipartFile> files = images == null ? List.of()
                : images.stream().filter(f -> f != null && !f.isEmpty()).toList();
        boolean hasContent = StringUtils.hasText(content);

        if (!hasContent && files.isEmpty()) {
            throw new AppException(ErrorCode.POST_EMPTY);
        }
        if (hasContent && content.length() > MAX_CONTENT_LENGTH) {
            throw new AppException(ErrorCode.INVALID_INPUT_FORMAT,
                    "Nội dung bài viết tối đa " + MAX_CONTENT_LENGTH + " ký tự");
        }
        if (files.size() > MAX_IMAGES) {
            throw new AppException(ErrorCode.POST_TOO_MANY_IMAGES,
                    "Mỗi bài viết tối đa " + MAX_IMAGES + " ảnh");
        }

        long userId = userService.findByEmailOrPhone(emailOrPhone).getId();
        LocalDateTime now = LocalDateTime.now();

        List<String> urls = files.isEmpty() ? List.of() : cloudinaryService.uploadImages(files, IMAGE_FOLDER);

        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setContent(hasContent ? content.trim() : null);
            post.setActive(true);
            post.setCreatedAt(now);

            List<PostImage> postImages = new ArrayList<>();
            for (String url : urls) {
                PostImage image = new PostImage();
                image.setPost(post);
                image.setImageUrl(url);
                image.setCreatedAt(now);
                postImages.add(image);
            }
            post.setPostImages(postImages);

            return toResponse(postRepository.save(post));
        } catch (RuntimeException e) {
            // Lưu DB lỗi thì xóa ảnh vừa upload để không bị rác trên Cloudinary
            cloudinaryService.deleteByUrls(urls);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(long id) {
        Post post = postRepository.findById(id)
                .filter(Post::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findByIsActiveTrue(pageable).map(this::toResponse);
    }

    private PostResponse toResponse(Post post) {
        List<String> imageUrls = post.getPostImages() == null ? List.of()
                : post.getPostImages().stream().map(PostImage::getImageUrl).toList();
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .imageUrls(imageUrls)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
