package com.dat_viet_group.datvietgroup.modules.social.service.serviceimpl;

import com.dat_viet_group.datvietgroup.modules.social.service.PostService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.dat_viet_group.datvietgroup.modules.social.dto.mapper.PostMapper;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.PostResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.Post;
import com.dat_viet_group.datvietgroup.modules.social.entity.PostImage;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String IMAGE_FOLDER = "posts";
    private static final String VIDEO_FOLDER = "posts/videos";
    private static final int MAX_IMAGES = 10;
    private static final int MAX_CONTENT_LENGTH = 1000;

    private final PostRepository postRepository;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(String emailOrPhone, String content, List<MultipartFile> images,
            MultipartFile video) {
        // Bỏ các part rỗng (form-data gửi "images" trống vẫn tạo ra 1 part rỗng)
        List<MultipartFile> files = images == null ? List.of()
                : images.stream().filter(f -> f != null && !f.isEmpty()).toList();
        boolean hasContent = StringUtils.hasText(content);
        boolean hasVideo = video != null && !video.isEmpty();

        if (!hasContent && files.isEmpty() && !hasVideo) {
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

        User author = userService.findByEmailOrPhone(emailOrPhone);
        long userId = author.getId();
        LocalDateTime now = LocalDateTime.now();

        // Upload video trước: video lỗi thì chưa có ảnh nào phải dọn
        String videoUrl = hasVideo ? cloudinaryService.uploadVideo(video, VIDEO_FOLDER) : null;
        List<String> urls;
        try {
            urls = files.isEmpty() ? List.of() : cloudinaryService.uploadImages(files, IMAGE_FOLDER);
        } catch (RuntimeException e) {
            cloudinaryService.deleteByUrl(videoUrl);
            throw e;
        }

        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setContent(hasContent ? content.trim() : null);
            post.setVideoUrl(videoUrl);
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

            return postMapper.toResponse(postRepository.save(post), author);
        } catch (RuntimeException e) {
            // Lưu DB lỗi thì xóa ảnh/video vừa upload để không bị rác trên Cloudinary
            cloudinaryService.deleteByUrls(urls);
            cloudinaryService.deleteByUrl(videoUrl);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(long id) {
        Post post = postRepository.findById(id)
                .filter(Post::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User author = userService.findAllByIds(List.of(post.getUserId())).stream().findFirst().orElse(null);
        return postMapper.toResponse(post, author);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<Post> page = postRepository.findByIsActiveTrue(pageable);
        // Lấy người đăng của cả trang trong 1 truy vấn, tránh gọi DB từng bài
        Set<Long> userIds = page.getContent().stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, User> users = userService.findAllByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return page.map(p -> postMapper.toResponse(p, users.get(p.getUserId())));
    }
}
