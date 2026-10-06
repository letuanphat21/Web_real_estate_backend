package com.dat_viet_group.datvietgroup.modules.news.entity;

import com.dat_viet_group.datvietgroup.modules.project.entity.Project;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Entity 
@Table (name = "news")
@AllArgsConstructor 
@NoArgsConstructor
public class News {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne 
    @JoinColumn (name = "project_id", nullable = false)
    private Project projectId;

    @ManyToOne 
    @JoinColumn (name = "category_new_id", nullable = false)
    private CategoryNew categoryNew;

    @Column (name = "title", length = 255)
    private String title;

    @Column (name = "content", length = 500)
    private String content;

    @Column (name = "active")
    private boolean active = true;
}
