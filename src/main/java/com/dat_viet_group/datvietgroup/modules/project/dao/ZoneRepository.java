package com.dat_viet_group.datvietgroup.modules.project.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.project.entity.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Page<Zone> findByProjectId(Long projectId, Pageable pageable);
}
