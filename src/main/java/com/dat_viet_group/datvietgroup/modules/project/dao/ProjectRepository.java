package com.dat_viet_group.datvietgroup.modules.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.project.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
