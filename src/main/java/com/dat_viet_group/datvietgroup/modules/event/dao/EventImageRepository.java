package com.dat_viet_group.datvietgroup.modules.event.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.event.entity.EventImage;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {

    List<EventImage> findByEventIdOrderByIdAsc(long eventId);
}
