package com.dat_viet_group.datvietgroup.modules.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.event.entity.Event;
import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Lọc theo trạng thái (null = tất cả) và từ khóa trong tiêu đề/địa điểm (null = bỏ qua) 
    @Query("""
            SELECT e FROM Event e
            WHERE (:status IS NULL OR e.status = :status)
              AND (:keyword IS NULL
                   OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Event> search(@Param("status") EventStatus status, @Param("keyword") String keyword, Pageable pageable);
}
