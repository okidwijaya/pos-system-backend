package com.kitadevelopers.pos.common.exception;

import com.kitadevelopers.pos.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                        ex.getValue() + "Isn't a valid UUID"
                ));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleProductNotFound(ProductNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errors.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage()));
    }
//    //validation err
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
//        return ResponseEntity
//                .badRequest()
//                .body(Map.of(
//                        "error", "Invalid ID format",
//                        "detail", ex.getValue() + " is not a valid UUID"
//                ));
//    }
//
//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<?> handleProductNotFound(ProductNotFoundException ex){
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(Map.of("error", ex.getMessage()));
//    }

    //    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){
    //        Map<String, String> errors = new HashMap<>();
    //
    //        ex.getBindingResult().getFieldErrors()
    //                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
    //
    //        return ResponseEntity.badRequest().body(errors);
    //    }
}
