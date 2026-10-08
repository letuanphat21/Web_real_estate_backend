package com.dat_viet_group.datvietgroup.modules.user.service.serviceimpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.user.dao.RoleRepository;
import com.dat_viet_group.datvietgroup.modules.user.dao.UserRepository;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.LoginRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.response.JwtAuthResponse;
import com.dat_viet_group.datvietgroup.modules.user.entity.Role;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.EmailService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        // Có "@" thì coi là email, ngược lại coi là số điện thoại
        Optional<User> found = emailOrPhone.contains("@")
                ? userRepository.findByEmail(emailOrPhone)
                : userRepository.findByPhone(emailOrPhone);

        User user = found.orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với email hoặc số điện thoại: " + emailOrPhone));

        if(user.isDeleted() == false) {
            throw new AppException(ErrorCode.USER_DELETED, "Tài khoản đã bị admin khóa");
        }
        if(user.isActive() == false) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE, "Tài khoản chưa được kích hoạt");
        }

        // Tên role trong DB có thể đã có hoặc chưa có tiền tố ROLE_, chỉ thêm khi thiếu
        String roleName = user.getRole().getName();
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return org.springframework.security.core.userdetails.User.builder()
                .username(emailOrPhone)
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(authority))
                .build();
    }

    @Override
    public void register(RegisterRequest request) {
       if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "Mật khẩu xác nhận không khớp!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Email này đã được sử dụng trên hệ thống");
        }

        if(userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED, "Số điện thoại này đã được sử dụng trên hệ thống");
        }

        LocalDateTime expiredTime = LocalDateTime.now().plusHours(24);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setDeleted(false);
        user.setActiveCode(generateOtp());
        user.setExpiredTime(expiredTime);
        user.setCreatedAt(LocalDateTime.now());

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                        "Không tìm thấy role ROLE_USER trong cơ sở dữ liệu"));
        user.setRole(role);

        userRepository.save(user);
        
        // Gửi email kích hoạt tài khoản 
        // emailService.guiEmailKichHoat(user.getEmail(), user.getActiveCode());

    }

    @Override
    public boolean kichHoatTaiKhoan(String maKichHoat) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'kichHoatTaiKhoan'");
    }

    @Override
    public boolean xacNhanOtp(String email, String otp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'xacNhanOtp'");
    }


    @Override
    public User findByEmailOrPhone(String emailOrPhone) {
        return emailOrPhone.contains("@")
                ? userRepository.findByEmail(emailOrPhone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với email: " + emailOrPhone))
                : userRepository.findByPhone(emailOrPhone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với số điện thoại: " + emailOrPhone));
    }


    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với id: " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAllByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : userRepository.findAllById(ids);
    }

}
