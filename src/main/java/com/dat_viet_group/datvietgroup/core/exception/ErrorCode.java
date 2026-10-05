package com.dat_viet_group.datvietgroup.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

@Getter 
@RequiredArgsConstructor
public enum ErrorCode {
    // --- CHUNG ---
    SUCCESS(HttpStatus.OK, "Thành công"),
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống không xác định"),
    INVALID_KEY(HttpStatus.BAD_REQUEST, "Mã lỗi (ErrorCode) cấu hình không hợp lệ"),
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào sai cấu trúc định dạng"),

    // --- XÁC THỰC & BẢO MẬT (AUTH / JWT) ---
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để thực hiện hành động này"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào tài nguyên này"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Mã Token đã hết hạn"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "Mã Token không hợp lệ"),

    // --- NGƯỜI DÙNG (USER) ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST, "Email này đã được sử dụng trên hệ thống"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu không chính xác");

    private final HttpStatus status;
    private final String messageTemplate;


    /** Hàm format câu thông báo động cực hay của bạn */
    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        return MessageFormat.format(messageTemplate, args);
    }

   
}

