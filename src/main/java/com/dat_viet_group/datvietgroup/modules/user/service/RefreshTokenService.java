package com.dat_viet_group.datvietgroup.modules.user.service;

import jakarta.servlet.http.HttpServletRequest;

/** Quản lý vòng đời refresh token trong bảng refresh_token (tạo, xoay vòng, thu hồi). */
public interface RefreshTokenService {

    /** Tạo refresh token mới cho user và lưu vào DB. Trả về chuỗi JWT để gắn vào cookie. */
    String issue(String emailOrPhone, HttpServletRequest request);

    /**
     * Đổi refresh token cũ lấy token mới (rotation). Token cũ bị thu hồi.
     * Nếu token cũ đã bị thu hồi mà vẫn được dùng lại, coi là bị đánh cắp: thu hồi mọi phiên của user rồi từ chối.
     */
    String rotate(String oldToken, String emailOrPhone, HttpServletRequest request);

    /** Thu hồi refresh token (logout). Token sai/không có trong DB thì bỏ qua. */
    void revoke(String token);

}
