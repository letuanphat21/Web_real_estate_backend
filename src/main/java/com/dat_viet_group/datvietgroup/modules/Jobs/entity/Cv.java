
package com.dat_viet_group.datvietgroup.modules.Jobs.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "cvs")
@NoArgsConstructor
@AllArgsConstructor
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    // Tham chiếu sang module User bằng ID, không import entity User
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cv")
    private List<Application> applications;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}