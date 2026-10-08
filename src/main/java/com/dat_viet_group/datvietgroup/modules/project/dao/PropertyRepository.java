package com.dat_viet_group.datvietgroup.modules.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.project.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    boolean existsByZoneIdAndPropertyCode(Long zoneId, String propertyCode);

    boolean existsByZoneIdAndPropertyCodeAndIdNot(Long zoneId, String propertyCode, Long id);
}
