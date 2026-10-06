package com.dat_viet_group.datvietgroup.modules.social.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Entity
@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
public class Post {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private long id;

        @Column(name = "content", length = 1000)
        private String content;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "is_active")
        private boolean isActive;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE,
                        CascadeType.PERSIST, CascadeType.REFRESH })
        private List<PostImage> postImages;

        @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE,
                        CascadeType.PERSIST, CascadeType.REFRESH })
        private List<Comment> comments;

        @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE,
                        CascadeType.PERSIST, CascadeType.REFRESH })
        private List<PostReaction> postReactions;

        @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE,
                        CascadeType.PERSIST, CascadeType.REFRESH })
        private List<PostReport> postReports;

}
