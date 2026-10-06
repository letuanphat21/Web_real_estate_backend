package com.dat_viet_group.datvietgroup.modules.news.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "NewsCategoryNew")
@Table(name = "category_news")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryNew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column (name ="slug", length = 200)
    private String slug; 

    @Column (name = "created_at")
    private LocalDateTime createdAt;   

    @Column (name = "active")
    private Boolean active;
}