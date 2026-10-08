package com.dat_viet_group.datvietgroup.modules.social.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.PostReaction;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

}
