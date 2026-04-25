package com.kitadevelopers.pos.common.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        Boolean success,
        T data,
        String error,
        LocalDateTime timeStamp
) {
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false, null, message, LocalDateTime.now());
    }
}
