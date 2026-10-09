package com.dat_viet_group.datvietgroup.modules.user.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.dat_viet_group.datvietgroup.modules.user.dto.request.LoginRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.response.JwtAuthResponse;
import com.dat_viet_group.datvietgroup.modules.user.dto.response.UserInfoResponse;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;



public interface UserService extends UserDetailsService {
    

    void register(RegisterRequest request);


    boolean kichHoatTaiKhoan(String maKichHoat);


    boolean xacNhanOtp(String email, String otp);

    User findByEmailOrPhone(String emailOrPhone);

    User findById(Long id);

    /** Lấy thông tin cá nhân của chính người đang đăng nhập (principalName = email/SĐT lấy từ token). */
    UserInfoResponse getMyInfo(String principalName);

    /** Lấy nhiều user theo danh sách id trong một lần truy vấn (id không tồn tại sẽ bị bỏ qua). */
    List<User> findAllByIds(Collection<Long> ids);

   
}
