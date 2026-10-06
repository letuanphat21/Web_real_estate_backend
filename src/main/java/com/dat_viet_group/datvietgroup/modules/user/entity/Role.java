package com.dat_viet_group.datvietgroup.modules.user.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH,
            CascadeType.DETACH })
    private List<User> users;
}
