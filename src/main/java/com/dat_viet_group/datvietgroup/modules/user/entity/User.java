package com.dat_viet_group.datvietgroup.modules.user.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;


    @Column (name = "active_code", length = 100)
    private String activeCode;

    @Column (name = "expired_time")
    private LocalDateTime expiredTime;


    @Column(name = "is_active")
    private boolean isActive;

    @Column (name = "is_deleted")
    private boolean isDeleted;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH,
            CascadeType.DETACH })
    private List<UserDevice> userDevices;
}
