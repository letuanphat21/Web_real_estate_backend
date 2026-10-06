package com.dat_viet_group.datvietgroup.core.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessageTemplate());
        this.errorCode = errorCode;
    }

    /** Dùng khi muốn tự ghi message, ví dụ: new AppException(ErrorCode.USER_NOT_FOUND, "Không có SĐT 0909...") */
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
