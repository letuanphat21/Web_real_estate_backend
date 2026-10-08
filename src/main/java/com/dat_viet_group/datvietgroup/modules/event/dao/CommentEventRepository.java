package com.dat_viet_group.datvietgroup.modules.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.event.entity.CommentEvent;

@Repository
public interface CommentEventRepository extends JpaRepository<CommentEvent, Long> {

    Page<CommentEvent> findByEventIdAndActiveTrue(long eventId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CommentEvent c WHERE c.event.id = :eventId")
    void deleteAllByEventId(@Param("eventId") long eventId);
}
