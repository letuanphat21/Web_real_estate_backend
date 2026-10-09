package com.dat_viet_group.datvietgroup.modules.news.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.modules.news.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    boolean existsByCategoryNewId(long categoryNewId);

    // Chỉ thêm điều kiện khi tham số có giá trị (tránh truyền null xuống PostgreSQL)
    static Specification<News> filter(Long categoryId, Long projectId, String keyword, Boolean active) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("categoryNew").get("id"), categoryId));
            }
            if (projectId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("projectId").get("id"), projectId));
            }
            if (active != null) {
                predicate = cb.and(predicate, cb.equal(root.get("active"), active));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("content")), pattern)));
            }
            return predicate;
        };
    }
}
