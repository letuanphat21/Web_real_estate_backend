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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Entity 
@Table (name = "event_members")
@NoArgsConstructor 
@AllArgsConstructor   
public class EventMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne 
    @JoinColumn (name = "event_id", nullable = false)
    private Event event;

    @Column (name = "joined_at")
    private LocalDateTime joinedAt;
}
