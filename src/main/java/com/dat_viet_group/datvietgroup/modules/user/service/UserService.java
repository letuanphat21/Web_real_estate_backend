package com.dat_viet_group.datvietgroup.modules.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;

public interface UserService extends UserDetailsService {

        void register(RegisterRequest request);
}
