package com.kitadevelopers.pos.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode code;
    private final HttpStatus status;

    public BusinessException(ErrorCode code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public static BusinessException badRequest(ErrorCode code, String message) {
        return new BusinessException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessException conflict(ErrorCode code, String message) {
        return new BusinessException(code, message, HttpStatus.CONFLICT);
    }

    public static BusinessException unauthorized(ErrorCode code, String message) {
        return new BusinessException(code, message, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException tooManyRequests(String message) {
        return new BusinessException(ErrorCode.RATE_LIMITED, message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
