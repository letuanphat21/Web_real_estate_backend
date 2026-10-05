package com.dat_viet_group.datvietgroup.core.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Cấu trúc response chuẩn dùng cho toàn bộ API.
 *
 * JSON trả về:
 * {
 *   "success": true,
 *   "message": "...",
 *   "data": { ... }   // null thì bị bỏ qua trong JSON
 * }
 *
 * Cách dùng:
 *   return ApiResponse.ok("Thành công");
 *   return ApiResponse.ok("Lấy danh sách thành công", danhSach);
 *   return ApiResponse.error(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
 *   return ApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
 */
@Getter 
@JsonInclude (JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor 
public class ApiResponse<T> {

    private final boolean success;
     private final int code;  
    private final String message;
    private final T data;

    
   /** Thành công, chỉ có message, không có data (Trả về mã 200) */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), message, null));
    }

    /** Thành công, có cả message lẫn data (Trả về mã 200) */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), message, data));
    }

    /** Thành công với HTTP 201 Created (Trả về mã 201) */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, HttpStatus.CREATED.value(), message, data));
    }

    // ================================================================
    // ERROR BASED ON ENUM — Lấy mã số nguyên trực tiếp từ Enum ErrorCode
    // ================================================================

    /** Lỗi tiêu chuẩn dựa trên cấu hình sẵn trong Enum (Lấy số từ status.value()) */
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ApiResponse<>(false, errorCode.getStatus().value(), errorCode.getMessageTemplate(), null));
    }

    /** Lỗi dựa trên Enum kèm theo dữ liệu bổ sung */
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode errorCode, T data) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ApiResponse<>(false, errorCode.getStatus().value(), errorCode.getMessageTemplate(), data));
    }

    /** Lỗi dùng câu thông báo tùy biến (sau khi bạn đã gọi hàm formatMessage) */
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode errorCode, String customMessage) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ApiResponse<>(false, errorCode.getStatus().value(), customMessage, null));
    }
    
}
