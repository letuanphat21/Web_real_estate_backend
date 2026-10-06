package com.dat_viet_group.datvietgroup.core.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.dat_viet_group.datvietgroup.core.common.ApiResponse;

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

    @ExceptionHandler(value = UsernameNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ApiResponse.error(ErrorCode.USER_NOT_FOUND, ex.getMessage());
    }
    

}
