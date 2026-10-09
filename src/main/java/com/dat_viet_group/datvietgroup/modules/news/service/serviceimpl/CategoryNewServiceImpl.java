package com.dat_viet_group.datvietgroup.modules.news.service.serviceimpl;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.news.dao.CategoryNewRepository;
import com.dat_viet_group.datvietgroup.modules.news.dao.NewsRepository;
import com.dat_viet_group.datvietgroup.modules.news.dto.request.CategoryNewRequest;
import com.dat_viet_group.datvietgroup.modules.news.dto.response.CategoryNewResponse;
import com.dat_viet_group.datvietgroup.modules.news.entity.CategoryNew;
import com.dat_viet_group.datvietgroup.modules.news.service.CategoryNewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryNewServiceImpl implements CategoryNewService {

    private final CategoryNewRepository categoryNewRepository;
    private final NewsRepository newsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryNewResponse> getAll(boolean onlyActive) {
        List<CategoryNew> categories = onlyActive
                ? categoryNewRepository.findByActiveTrueOrderByNameAsc()
                : categoryNewRepository.findAllByOrderByNameAsc();
        return categories.stream().map(CategoryNewResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryNewResponse getById(long id) {
        return CategoryNewResponse.from(findCategory(id));
    }

    @Override
    @Transactional
    public CategoryNewResponse create(CategoryNewRequest request) {
        String slug = resolveSlug(request);
        if (categoryNewRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.CATEGORY_NEW_SLUG_EXISTED);
        }

        CategoryNew category = new CategoryNew();
        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setActive(request.getActive() == null || request.getActive());
        category.setCreatedAt(LocalDateTime.now());
        return CategoryNewResponse.from(categoryNewRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryNewResponse update(long id, CategoryNewRequest request) {
        CategoryNew category = findCategory(id);
        String slug = resolveSlug(request);
        if (categoryNewRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.CATEGORY_NEW_SLUG_EXISTED);
        }

        category.setName(request.getName().trim());
        category.setSlug(slug);
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        return CategoryNewResponse.from(categoryNewRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(long id) {
        CategoryNew category = findCategory(id);
        // Còn tin tức thuộc danh mục thì không cho xóa (news.category_new_id NOT NULL), chỉ nên ẩn
        if (newsRepository.existsByCategoryNewId(id)) {
            throw new AppException(ErrorCode.CATEGORY_NEW_IN_USE);
        }
        categoryNewRepository.delete(category);
    }

    private CategoryNew findCategory(long id) {
        return categoryNewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NEW_NOT_FOUND));
    }

    private String resolveSlug(CategoryNewRequest request) {
        return StringUtils.hasText(request.getSlug()) ? request.getSlug() : toSlug(request.getName());
    }

    // "Tin Thị Trường Đất Việt" -> "tin-thi-truong-dat-viet" 
    private String toSlug(String input) {
        String noAccent = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd');
        return noAccent.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
