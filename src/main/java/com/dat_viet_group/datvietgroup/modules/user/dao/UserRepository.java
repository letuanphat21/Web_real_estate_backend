package com.dat_viet_group.datvietgroup.modules.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByUserName(String username);
}
