package com.dat_viet_group.datvietgroup.modules.user.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;



public interface UserService extends UserDetailsService {
    

    void register(RegisterRequest request);


    boolean kichHoatTaiKhoan(String maKichHoat);


    boolean xacNhanOtp(String email, String otp);

    User findByEmailOrPhone(String emailOrPhone);

    User findById(Long id);

}
