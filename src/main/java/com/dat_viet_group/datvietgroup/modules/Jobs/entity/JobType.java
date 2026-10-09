
package com.dat_viet_group.datvietgroup.modules.Jobs.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;// tự sinh setter, getter, toString, equals, hasCode
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@Entity
@Table (name = "job_types")
@NoArgsConstructor //Sinh constructor rỗng new JobType(). JPA bắt buộc có để Hibernate tạo object khi đọc từ DB
@AllArgsConstructor //Sinh constructor có tham số 
@SQLRestriction("deleted_at IS NULL") // xóa mềm: truy vấn tự bỏ qua bản ghi đã xóa
public class JobType {

    @Id // đánh dấu khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự động tăng
    @Column(name = "id")
    private long id;

    @Column(name = "name", length = 100) // độ dài tối đa 100 ký tự
    private String name;

    @Column(name="is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "jobType")
    private List<Job> jobs;

    @PrePersist
    void onCreate(){
        createdAt = LocalDateTime.now();
    }
    

}