package com.dat_viet_group.datvietgroup.modules.event.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.modules.event.entity.Event;
import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    // Chỉ thêm điều kiện khi tham số có giá trị — tránh truyền null xuống PostgreSQL
    // (null bị đoán kiểu bytea => lỗi "function lower(bytea) does not exist")
    static Specification<Event> filter(EventStatus status, String keyword) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("location")), pattern)));
            }
            return predicate;
        };
    }
}
