package com.dat_viet_group.datvietgroup.modules.user.service.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.user.dao.RefreshTokenRepository;
import com.dat_viet_group.datvietgroup.modules.user.entity.RefreshToken;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.JWTService;
import com.dat_viet_group.datvietgroup.modules.user.service.RefreshTokenService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;
    private final UserService userService;

    @Override
    @Transactional
    public String issue(String emailOrPhone, HttpServletRequest request) {
        return issueAndSave(emailOrPhone, request).getKey();
    }

    @Override
    @Transactional(noRollbackFor = AppException.class)
    public String rotate(String oldToken, String emailOrPhone, HttpServletRequest request) {
        RefreshToken old = refreshTokenRepository.findByJti(jwtService.extractJti(oldToken))
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_REFRESH_INVALID));

        if (old.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(old.getUser().getId());
            throw new AppException(ErrorCode.TOKEN_REFRESH_INVALID);
        }

        var created = issueAndSave(emailOrPhone, request);
        old.setRevoked(true);
        old.setReplacedByJti(created.getValue());
        refreshTokenRepository.save(old);
        return created.getKey();
    }

    @Override
    @Transactional
    public void revoke(String token) {
        String jti;
        try {
            jti = jwtService.extractJti(token);
        } catch (ExpiredJwtException e) {
            jti = e.getClaims().getId(); // token hết hạn vẫn thu hồi được
        } catch (JwtException e) {
            return;
        }
        refreshTokenRepository.findByJti(jti).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    /** Trả về cặp (chuỗi JWT, jti). */
    private java.util.Map.Entry<String, String> issueAndSave(String emailOrPhone, HttpServletRequest request) {
        String token = jwtService.createRefreshToken(emailOrPhone);
        User user = userService.findByEmailOrPhone(emailOrPhone);

        RefreshToken entity = new RefreshToken();
        entity.setJti(jwtService.extractJti(token));
        entity.setUser(user);
        entity.setExpiresAt(LocalDateTime.ofInstant(
                jwtService.extractExpiration(token).toInstant(), ZoneId.systemDefault()));
        entity.setRevoked(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIpAddress(request.getRemoteAddr());
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            entity.setUserAgent(userAgent.length() > 255 ? userAgent.substring(0, 255) : userAgent);
        }
        refreshTokenRepository.save(entity);
        return java.util.Map.entry(token, entity.getJti());
    }
}
