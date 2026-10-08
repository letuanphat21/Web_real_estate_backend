package com.dat_viet_group.datvietgroup.modules.social.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.social.entity.UserFollow;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    @Query("SELECT f FROM UserFollow f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    Optional<UserFollow> findByFollowerAndFollowing(@Param("followerId") long followerId,
            @Param("followingId") long followingId);

    @Query("SELECT COUNT(f) > 0 FROM UserFollow f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    boolean isFollowing(@Param("followerId") long followerId, @Param("followingId") long followingId);

    /** Những người đang theo dõi userId. */
    @Query("SELECT f FROM UserFollow f WHERE f.followingId = :userId")
    Page<UserFollow> findFollowers(@Param("userId") long userId, Pageable pageable);

    /** Những người userId đang theo dõi. */
    @Query("SELECT f FROM UserFollow f WHERE f.followerId = :userId")
    Page<UserFollow> findFollowing(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM UserFollow f WHERE f.followingId = :userId")
    long countFollowers(@Param("userId") long userId);

    @Query("SELECT COUNT(f) FROM UserFollow f WHERE f.followerId = :userId")
    long countFollowing(@Param("userId") long userId);
}
