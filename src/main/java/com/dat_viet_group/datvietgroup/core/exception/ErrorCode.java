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
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Mã Token đã hết hạn"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Mã Token không hợp lệ"),

    // --- NGƯỜI DÙNG (USER) ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST, "Email này đã được sử dụng trên hệ thống"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu không chính xác"),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "Tài khoản chưa được kích hoạt"),
    USER_DELETED(HttpStatus.BAD_REQUEST, "Tài khoản đã bị admin khóa"),
    PHONE_EXISTED(HttpStatus.BAD_REQUEST, "Số điện thoại này đã được sử dụng trên hệ thống"),

    // --- UPLOAD ẢNH (CLOUDINARY) ---
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "Chưa chọn ảnh hoặc file rỗng"),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "File không phải là ảnh hợp lệ"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "Dung lượng ảnh vượt quá giới hạn cho phép"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload ảnh lên Cloudinary thất bại"),

    // --- BÀI VIẾT (SOCIAL) ---
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"),
    POST_EMPTY(HttpStatus.BAD_REQUEST, "Bài viết phải có nội dung hoặc ít nhất 1 ảnh"),
    POST_TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "Số lượng ảnh vượt quá giới hạn cho phép"),

    // --- BÌNH LUẬN (SOCIAL) ---
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"),
    COMMENT_EMPTY(HttpStatus.BAD_REQUEST, "Nội dung bình luận không được để trống"),
    COMMENT_PARENT_INVALID(HttpStatus.BAD_REQUEST, "Bình luận cha không thuộc bài viết này"),

    // --- THEO DÕI (SOCIAL) ---
    FOLLOW_SELF(HttpStatus.BAD_REQUEST, "Bạn không thể tự theo dõi chính mình");

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

