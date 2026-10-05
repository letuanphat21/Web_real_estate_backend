package com.dat_viet_group.datvietgroup.modules.user.service.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dat_viet_group.datvietgroup.modules.user.entity.Role;
import com.dat_viet_group.datvietgroup.modules.user.dao.RoleRepository;
import com.dat_viet_group.datvietgroup.modules.user.dao.UserRepository;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với tên đăng nhập: " + userName));

        // if (!user.isDaKichHoat()) {
        // throw new RuntimeException("Tài khoản chưa được kích hoạt");
        // }

        // if (!user.isActive()) {
        // throw new RuntimeException("Tài khoản đã bị khóa");
        // }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getTenQuyen()))
                .toList();

        // Này là UserDetail của security nha
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                authorities);
    }

    @Override
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        Optional<User> existingUser = userRepository.findByUserName(request.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Optional<Role> roleUser = roleRepository.findByTenQuyen("ROLE_USER");
        roleUser.ifPresent(role -> user.setRoles(List.of(role)));

        userRepository.save(user);
    }

}
