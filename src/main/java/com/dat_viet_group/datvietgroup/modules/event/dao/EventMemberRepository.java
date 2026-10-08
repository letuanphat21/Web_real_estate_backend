package com.dat_viet_group.datvietgroup.modules.event.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.event.entity.EventMember;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    List<EventMember> findByEventIdOrderByJoinedAtAsc(long eventId);

    Optional<EventMember> findByEventIdAndUserId(long eventId, long userId);

    boolean existsByEventIdAndUserId(long eventId, long userId);

    long countByEventId(long eventId);

    @Modifying
    @Query("DELETE FROM EventMember m WHERE m.event.id = :eventId")
    void deleteAllByEventId(@Param("eventId") long eventId);
}
