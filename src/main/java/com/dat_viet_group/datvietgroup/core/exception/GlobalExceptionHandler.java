package com.dat_viet_group.datvietgroup.core.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.dat_viet_group.datvietgroup.core.common.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ApiResponse.error(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getMessage());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage()
                : ErrorCode.INVALID_INPUT_FORMAT.getMessageTemplate();
        return ApiResponse.error(ErrorCode.INVALID_INPUT_FORMAT, errorMessage);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Chi tiết parse chỉ ghi vào log, client chỉ thấy thông báo chung
        log.warn("Request body không đọc được: {}", ex.getMessage());
        return ApiResponse.error(ErrorCode.INVALID_INPUT_FORMAT);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Vi phạm ràng buộc dữ liệu: {}", ex.getMessage());
        return ApiResponse.error(ErrorCode.DATA_CONFLICT);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ApiResponse.error(ErrorCode.USER_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        // Client chỉ thấy thông báo chung, nguyên nhân thật chỉ ghi vào log
        log.warn("Xác thực thất bại: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        // DaoAuthenticationProvider bọc mọi exception khác UsernameNotFoundException (vd AppException
        // từ loadUserByUsername) vào InternalAuthenticationServiceException, cần bóc ra lại
        if (ex.getCause() instanceof AppException appException) {
            return handleAppException(appException);
        }
        return ApiResponse.error(ErrorCode.INVALID_CREDENTIALS);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex) {
        return ApiResponse.error(ErrorCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler(value = JwtException.class)
    ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
        return ApiResponse.error(ErrorCode.TOKEN_INVALID);
    }

}
