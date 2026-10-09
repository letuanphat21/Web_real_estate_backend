package com.dat_viet_group.datvietgroup.modules.user.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.user.entity.RefreshToken;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByJti(String jti);

    // Thu hồi mọi phiên của user (khi phát hiện token cũ bị dùng lại)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
    int revokeAllByUserId(@Param("userId") Long userId);

    // Dọn các token đã hết hạn
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);

}
