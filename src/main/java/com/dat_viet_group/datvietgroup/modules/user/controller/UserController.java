package com.dat_viet_group.datvietgroup.modules.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.LoginRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.request.RegisterRequest;
import com.dat_viet_group.datvietgroup.modules.user.dto.response.JwtAuthResponse;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.JWTService;
import com.dat_viet_group.datvietgroup.modules.user.service.RefreshTokenService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    // jwt.refresh-expiration tính bằng mili giây
    @Value ("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Value ("${app.cookie.secure}")
    private boolean cookieSecure;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok("Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt tài khoản.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> loginUser(@Validated @RequestBody LoginRequest loginRequest,
            HttpServletRequest request, HttpServletResponse response) {
        // Sai tài khoản/mật khẩu thì authenticate() tự ném AuthenticationException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmailOrPhone(), loginRequest.getPassword()));

        String token = jwtService.generateToken(loginRequest.getEmailOrPhone());
        String refreshToken = refreshTokenService.issue(loginRequest.getEmailOrPhone(), request);
        addRefreshCookie(response, refreshToken, refreshExpirationMs / 1000);

        return ApiResponse.ok("Đăng nhập thành công!", new JwtAuthResponse(token));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new AppException(ErrorCode.TOKEN_REFRESH_INVALID);
        }

        try {
            Long userId = jwtService.extractUserId(refreshToken);
            User user = userService.findById(userId);
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
            if (!jwtService.validateRefreshToken(refreshToken, userDetails)) {
                throw new AppException(ErrorCode.TOKEN_REFRESH_INVALID);
            }

            String newAccessToken = jwtService.generateToken(user.getEmail());
            // Thu hồi token cũ, cấp token mới (rotation)
            String newRefreshToken = refreshTokenService.rotate(refreshToken, user.getEmail(), request);
            addRefreshCookie(response, newRefreshToken, refreshExpirationMs / 1000);

            return ApiResponse.ok("Làm mới token thành công!", new JwtAuthResponse(newAccessToken));
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.TOKEN_REFRESH_EXPIRED);
        } catch (JwtException e) {
            throw new AppException(ErrorCode.TOKEN_REFRESH_INVALID);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            refreshTokenService.revoke(refreshToken);
        }
        // Luôn xóa cookie, kể cả khi không có token
        addRefreshCookie(response, "", 0);

        return ApiResponse.ok("Đăng xuất thành công!");
    }

    private void addRefreshCookie(HttpServletResponse response, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/users")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }



}
