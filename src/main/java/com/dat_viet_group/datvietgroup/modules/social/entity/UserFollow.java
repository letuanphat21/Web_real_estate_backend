package com.dat_viet_group.datvietgroup.modules.social.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** followerId theo dõi followingId. Chỉ lưu id user, không liên kết sang entity của module user. */
@Getter
@Setter
@Entity
@Table(name = "user_follow",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_follow_follower_following", columnNames = {
                "follower_id", "following_id" }),
        indexes = @Index(name = "idx_user_follow_following", columnList = "following_id"))
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /** Người đi theo dõi. */
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    /** Người được theo dõi. */
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
