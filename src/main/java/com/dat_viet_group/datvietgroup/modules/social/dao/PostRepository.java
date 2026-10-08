package com.dat_viet_group.datvietgroup.modules.social.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.Post;


@Repository 
public interface PostRepository extends JpaRepository<Post, Long> {
    
}
