package com.dat_viet_group.datvietgroup.modules.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.user.entity.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> {


    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findByPhone(String phone);

    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);


}
