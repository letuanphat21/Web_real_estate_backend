package com.dat_viet_group.datvietgroup.modules.social.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Bình luận gốc (không có cha) của một bài viết. */
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.isActive = true")
    Page<Comment> findRootComments(@Param("postId") long postId, Pageable pageable);

    /** Các trả lời của một bình luận. */
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId AND c.isActive = true")
    Page<Comment> findReplies(@Param("parentId") long parentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentId AND c.isActive = true")
    long countReplies(@Param("parentId") long parentId);
}
