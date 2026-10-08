package com.dat_viet_group.datvietgroup.modules.social.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.PostImage;

@Repository 
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    
}
