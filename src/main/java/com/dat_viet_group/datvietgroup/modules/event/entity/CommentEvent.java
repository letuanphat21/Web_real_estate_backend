package com.dat_viet_group.datvietgroup.modules.event.entity;
import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity 
@Table (name = "comment_events")
public class CommentEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    
    @ManyToOne
    @JoinColumn (name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column (name= "content", length = 500)
    private String content;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "active")
    private Boolean active;
}
