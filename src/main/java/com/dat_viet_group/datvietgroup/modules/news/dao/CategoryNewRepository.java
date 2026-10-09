package com.dat_viet_group.datvietgroup.modules.news.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.news.entity.CategoryNew;

@Repository
public interface CategoryNewRepository extends JpaRepository<CategoryNew, Long> {

    List<CategoryNew> findByActiveTrueOrderByNameAsc();

    List<CategoryNew> findAllByOrderByNameAsc();

    Optional<CategoryNew> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, long id);
}
