package com.dat_viet_group.datvietgroup.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.LoginRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.response.JwtAuthResponse;
import com.dat_viet_group.datvietgroup.modules.user.service.JWTService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok("Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt tài khoản.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> loginUser(@Validated @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        // Sai tài khoản/mật khẩu thì authenticate() tự ném AuthenticationException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmailOrPhone(), loginRequest.getPassword()));

        String token = jwtService.generateToken(loginRequest.getEmailOrPhone());
        String refreshToken = jwtService.createRefreshToken(loginRequest.getEmailOrPhone());

        // Cookie cookie = new Cookie("refreshToken", refreshToken);
        // cookie.setHttpOnly(true);
        // cookie.setPath("/");
        // cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        // // cookie.setSecure(true); // Bỏ comment nếu chạy HTTPS
        // response.addCookie(cookie);
        System.out.println("Refresh token: " + refreshToken);

        return ApiResponse.ok("Đăng nhập thành công!", new JwtAuthResponse(token));
    }
}
