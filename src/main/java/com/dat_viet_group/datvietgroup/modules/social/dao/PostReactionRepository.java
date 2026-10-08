package com.dat_viet_group.datvietgroup.modules.social.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.PostReaction;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    /** Lượt thích (reaction = true) của user cho một bài viết. */
    @Query("SELECT r FROM PostReaction r WHERE r.post.id = :postId AND r.userId = :userId")
    Optional<PostReaction> findByPostAndUser(@Param("postId") long postId, @Param("userId") long userId);

    @Query("SELECT COUNT(r) FROM PostReaction r WHERE r.post.id = :postId AND r.reaction = true")
    long countLikes(@Param("postId") long postId);
}
