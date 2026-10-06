package com.dat_viet_group.datvietgroup.modules.user.service;

import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JWTService {
    String generateToken(String email);

    String createRefreshToken(String email);

    <T> T extractClaim(String token, Function<Claims, T> claimsTFunction);

    Date extractExpiration(String token);

    String extractUserName(String token);

    Long extractUserId(String token);

    Boolean validateToken(String token, UserDetails userDetails);
}
