package com.dat_viet_group.datvietgroup.modules.news.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.news.dao.CategoryNewRepository;
import com.dat_viet_group.datvietgroup.modules.news.dao.NewImageRepository;
import com.dat_viet_group.datvietgroup.modules.news.dao.NewsRepository;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.NewsRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewImageResponse;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.NewsResponse;
import com.dat_viet_group.datvietgroup.modules.news.entity.CategoryNew;
import com.dat_viet_group.datvietgroup.modules.news.entity.NewImage;
import com.dat_viet_group.datvietgroup.modules.news.entity.News;
import com.dat_viet_group.datvietgroup.modules.news.service.NewsService;
import com.dat_viet_group.datvietgroup.modules.project.dao.ProjectRepository;
import com.dat_viet_group.datvietgroup.modules.project.entity.Project;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final String IMAGE_FOLDER = "news";

    private final NewsRepository newsRepository;
    private final NewImageRepository newImageRepository;
    private final CategoryNewRepository categoryNewRepository;
    private final ProjectRepository projectRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NewsResponse> getAll(Long categoryId, Long projectId, String keyword, Boolean active,
            Pageable pageable) {
        return PageResponse.from(
                newsRepository.findAll(NewsRepository.filter(categoryId, projectId, keyword, active), pageable),
                this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponse getById(long id, boolean onlyActive) {
        News news = findNews(id);
        if (onlyActive && !news.isActive()) {
            throw new AppException(ErrorCode.NEWS_NOT_FOUND);
        }
        return toResponse(news);
    }

    @Override
    @Transactional
    public NewsResponse create(NewsRequest request, List<MultipartFile> images, long authorId) {
        User author = userService.findById(authorId);

        News news = new News();
        applyRequest(news, request);
        news.setUser(author);
        news.setActive(request.getActive() == null || request.getActive());
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
        newsRepository.save(news);

        if (images != null && images.stream().anyMatch(f -> f != null && !f.isEmpty())) {
            saveImages(news, images);
        }
        return toResponse(news);
    }

    @Override
    @Transactional
    public NewsResponse update(long id, NewsRequest request) {
        News news = findNews(id);
        applyRequest(news, request);
        if (request.getActive() != null) {
            news.setActive(request.getActive());
        }
        news.setUpdatedAt(LocalDateTime.now());
        return toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsResponse updateActive(long id, boolean active) {
        News news = findNews(id);
        news.setActive(active);
        news.setUpdatedAt(LocalDateTime.now());
        return toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public void delete(long id) {
        News news = findNews(id);
        List<NewImage> images = newImageRepository.findByNewsIdOrderByIdAsc(id);
        List<String> urls = images.stream().map(NewImage::getImageUrl).toList();

        newImageRepository.deleteAll(images);
        newsRepository.delete(news);
        deleteFromCloudinaryAfterCommit(urls);
    }

    @Override
    @Transactional
    public List<NewImageResponse> addImages(long newsId, List<MultipartFile> images) {
        News news = findNews(newsId);
        return saveImages(news, images).stream().map(NewImageResponse::from).toList();
    }

    @Override
    @Transactional
    public void deleteImage(long newsId, long imageId) {
        NewImage image = newImageRepository.findById(imageId)
                .filter(img -> img.getNews().getId() == newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_IMAGE_NOT_FOUND));
        newImageRepository.delete(image);
        deleteFromCloudinaryAfterCommit(List.of(image.getImageUrl()));
    }

    // HELPER
    private News findNews(long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_FOUND));
    }

    private void applyRequest(News news, NewsRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
        CategoryNew category = categoryNewRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NEW_NOT_FOUND));

        news.setProjectId(project);
        news.setCategoryNew(category);
        news.setTitle(request.getTitle().trim());
        news.setContent(request.getContent().trim());
    }

    // Upload lên Cloudinary rồi lưu DB; DB lỗi hoặc transaction rollback thì xóa ảnh vừa upload 
    private List<NewImage> saveImages(News news, List<MultipartFile> files) {
        List<String> urls = cloudinaryService.uploadImages(files, IMAGE_FOLDER);
        try {
            List<NewImage> images = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                NewImage image = new NewImage();
                image.setNews(news);
                image.setTitle(files.get(i).getOriginalFilename());
                image.setImageUrl(urls.get(i));
                image.setCreatedAt(LocalDateTime.now());
                images.add(image);
            }
            List<NewImage> saved = newImageRepository.saveAll(images);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        cloudinaryService.deleteByUrls(urls);
                    }
                }
            });
            return saved;
        } catch (RuntimeException e) {
            cloudinaryService.deleteByUrls(urls);
            throw e;
        }
    }

    // Chỉ xóa ảnh trên Cloudinary khi DB đã commit thành công
    private void deleteFromCloudinaryAfterCommit(List<String> urls) {
        if (urls.isEmpty()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cloudinaryService.deleteByUrls(urls);
            }
        });
    }

    private NewsResponse toResponse(News news) {
        CategoryNew category = news.getCategoryNew();
        Project project = news.getProjectId();
        User author = news.getUser();
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .active(news.isActive())
                .category(new NewsResponse.CategorySummary(category.getId(), category.getName(), category.getSlug()))
                .project(new NewsResponse.ProjectSummary(project.getId(), project.getName()))
                .author(new NewsResponse.AuthorSummary(author.getId(), author.getFullName(), author.getAvatarUrl()))
                .images(newImageRepository.findByNewsIdOrderByIdAsc(news.getId()).stream()
                        .map(NewImageResponse::from)
                        .toList())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
