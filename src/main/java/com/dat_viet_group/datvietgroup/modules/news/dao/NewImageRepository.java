package com.dat_viet_group.datvietgroup.modules.news.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.news.entity.NewImage;

@Repository
public interface NewImageRepository extends JpaRepository<NewImage, Long> {

    List<NewImage> findByNewsIdOrderByIdAsc(long newsId);
}
