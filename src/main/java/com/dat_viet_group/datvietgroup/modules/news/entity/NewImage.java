package com.dat_viet_group.datvietgroup.modules.news.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table (name = "new_images")
@NoArgsConstructor 
@AllArgsConstructor  
public class NewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn (name = "new_id", nullable = false)
    private News news;

    @Column (name = "title", length = 200)
    private String title;

    @Column (name = "image_url")
    private String imageUrl;

    @Column (name = "created_at")
    private LocalDateTime createdAt;
}
