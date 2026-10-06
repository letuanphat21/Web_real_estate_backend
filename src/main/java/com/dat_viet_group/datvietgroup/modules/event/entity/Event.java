package com.dat_viet_group.datvietgroup.modules.event.entity;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table (name = "events")
@NoArgsConstructor 
@AllArgsConstructor   
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column (name = "title", length = 200)
    private String title;

    @Column (name = "content", length = 500)
    private String content;

    @Column (name = "location", length = 200)
    private String location;

    @Column (name = "max_attendees")
    private int maxAttendees;

    @Column (name = "start_time")
    private LocalDateTime startTime;

    @Column (name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private EventStatus status;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_at")
    private LocalDateTime updatedAt;
}
