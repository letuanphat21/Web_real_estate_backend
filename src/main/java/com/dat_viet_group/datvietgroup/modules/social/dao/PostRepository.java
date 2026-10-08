package com.dat_viet_group.datvietgroup.modules.social.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isActive = true")
    Page<Post> findByIsActiveTrue(Pageable pageable);
}
