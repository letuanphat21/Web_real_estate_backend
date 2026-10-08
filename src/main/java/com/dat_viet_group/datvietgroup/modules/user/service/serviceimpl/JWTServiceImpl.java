package com.dat_viet_group.datvietgroup.modules.user.service.serviceimpl;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.JWTService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service 
public class JWTServiceImpl implements JWTService{

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    @Autowired 
    private UserService userService;

    @Override
    public String generateToken(String emailOrPhone) {
        User user = userService.findByEmailOrPhone(emailOrPhone);
        Date now = new Date();

        return Jwts.builder()
                .subject(emailOrPhone)
                .claim("userId", user.getId())
                .claim("avatar", user.getAvatarUrl())
                .claim("role", user.getRole().getName())
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String createRefreshToken(String emailOrPhone) {
        User user = userService.findByEmailOrPhone(emailOrPhone);
        Date now = new Date();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(emailOrPhone)
                .claim("userId", user.getId())
                .claim("role", user.getRole().getName())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        // Chỉ access token mới được dùng để gọi API, refresh token bị từ chối
        if (!"access".equals(extractClaim(token, claims -> claims.get("type", String.class)))) {
            throw new io.jsonwebtoken.JwtException("Token không phải access token");
        }
        // Hạn token đã được parser kiểm tra (ném ExpiredJwtException) khi đọc claims
        Long userId = extractUserId(token);
        User user = userService.findById(userId);
        String username = userDetails.getUsername();
        return username.equals(user.getEmail()) || username.equals(user.getPhone());
    }

    @Override
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    @Override 
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        // Chỉ refresh token mới được dùng để làm mới access token, access token bị từ chối
        if (!"refresh".equals(extractClaim(refreshToken, claims -> claims.get("type", String.class)))) {
            throw new io.jsonwebtoken.JwtException("Token không phải refresh token");
        }
        // Hạn token đã được parser kiểm tra (ném ExpiredJwtException) khi đọc claims
        Long userId = extractUserId(refreshToken);
        User user = userService.findById(userId);
        String username = userDetails.getUsername();
        return username.equals(user.getEmail()) || username.equals(user.getPhone());
    }
    
}
